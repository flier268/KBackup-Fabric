package com.keuin.kbackupfabric.command;

import com.keuin.kbackupfabric.metadata.BackupMetadata;
import com.keuin.kbackupfabric.metadata.MetadataHolder;
import com.keuin.kbackupfabric.operation.AbstractConfirmableOperation;
import com.keuin.kbackupfabric.util.naming.BackupFilesystemUtil;
import com.keuin.kbackupfabric.suggestion.BackupNameSuggestionProvider;
import com.keuin.kbackupfabric.util.naming.BackupNameTimeFormatter;
import com.keuin.kbackupfabric.util.PrintUtil;
import com.keuin.kbackupfabric.worker.BackupWorker;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.keuin.kbackupfabric.util.naming.BackupFilesystemUtil.*;
import static com.keuin.kbackupfabric.util.PrintUtil.*;

public final class CommandMain {


    private static final int SUCCESS = 1;
    private static final int FAILED = -1;

    //private static final Logger LOGGER = LogManager.getLogger();

    private static final List<String> backupNameList = new ArrayList<>(); // index -> backupName
    private static AbstractConfirmableOperation pendingOperation = null;

    /**
     * Print the help menu.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int help(CommandContext<ServerCommandSource> context) {
        msgInfo(context, "==== KBackup Manual ====");
        msgInfo(context, "/kb       /kb help        Print help menu.");
        msgInfo(context, "/kb list                  Show all backups.");
        msgInfo(context, "/kb backup [backup_name]  Backup the whole level to backup_name. The default name is current system time.");
        msgInfo(context, "/kb restore <backup_name> Delete the whole current level and restore from given backup. /kb restore is identical with /kb list.");
        msgInfo(context, "/kb confirm               Confirm and start restoring.");
        msgInfo(context, "/kb cancel                Cancel the restoration to be confirmed. If cancelled, /kb confirm will not run.");
        return SUCCESS;
    }

    /**
     * Print the help menu. (May show extra info during the first run after restoring)
     *
     * @param context the context.
     * @return stat code.
     */
    public static int kb(CommandContext<ServerCommandSource> context) {
        int statCode = list(context);
        if (MetadataHolder.hasMetadata()) {
            // Output metadata info
            msgStress(context, "Restored from backup " + MetadataHolder.getMetadata().getBackupName());
        }
        return statCode;
    }

    /**
     * List all existing backups.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int list(CommandContext<ServerCommandSource> context) {
        msgInfo(context, "Available backups: (file is not checked, manipulation may affect this plugin)");
        MinecraftServer server = context.getSource().getMinecraftServer();
        File[] files = getBackupSaveDirectory(server).listFiles(
                (dir, name) -> dir.isDirectory() && name.toLowerCase().endsWith(".zip") && name.toLowerCase().startsWith(getBackupFileNamePrefix())
        );
        backupNameList.clear();
        if (files != null) {
            int i = 0;
            for (File file : files) {
                ++i;
                String backupName = getBackupName(file.getName());
                backupNameList.add(backupName);
                msgInfo(context, String.format("[%d] %s, size: %.1fMB", i, backupName, file.length() * 1.0 / 1024 / 1024));
            }
        }
        return SUCCESS;
    }

    /**
     * Backup with context parameter backupName.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int backup(CommandContext<ServerCommandSource> context) {
        //KBMain.backup("name")
        String backupName = StringArgumentType.getString(context, "backupName");
        if (backupName.matches("[0-9]*")) {
            // Numeric param is not allowed
            backupName = String.format("a%s", backupName);
            msgWarn(context, String.format("Pure numeric name is not allowed. Renaming to %s", backupName));
        }
        return doBackup(context, backupName);
    }

    /**
     * Delete an existing backup with context parameter backupName.
     * Simply set the pending backupName to given backupName, for the second confirmation.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int delete(CommandContext<ServerCommandSource> context) {

        String backupName = parseBackupName(context, StringArgumentType.getString(context, "backupName"));
        MinecraftServer server = context.getSource().getMinecraftServer();

        if (backupName == null)
            return list(context); // Show the list and return

        // Validate backupName
        if (!isBackupNameValid(backupName, server)) {
            // Invalid backupName
            msgErr(context, "Invalid backup name! Please check your input. The list index number is also valid.");
            return FAILED;
        }

        // Update pending task
        pendingOperation = AbstractConfirmableOperation.createDeleteOperation(context, backupName);

        msgWarn(context, String.format("DELETION WARNING: The deletion is irreversible! You will lose the backup %s permanently. Use /kb confirm to start or /kb cancel to abort.", backupName), true);
        return SUCCESS;
    }


    /**
     * Restore with context parameter backupName.
     * Simply set the pending backupName to given backupName, for the second confirmation.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int restore(CommandContext<ServerCommandSource> context) {
        //KBMain.restore("name")
        MinecraftServer server = context.getSource().getMinecraftServer();
        String backupName = parseBackupName(context, StringArgumentType.getString(context, "backupName"));
        backupName = parseBackupName(context, backupName);

        if (backupName == null)
            return list(context); // Show the list and return

        // Validate backupName
        if (!isBackupNameValid(backupName, server)) {
            // Invalid backupName
            msgErr(context, "Invalid backup name! Please check your input. The list index number is also valid.", false);
            return FAILED;
        }

        // Update pending task
        pendingOperation = AbstractConfirmableOperation.createRestoreOperation(context, backupName);

        msgWarn(context, String.format("RESET WARNING: You will LOSE YOUR CURRENT WORLD PERMANENTLY! The worlds will be replaced with backup %s . Use /kb confirm to start or /kb cancel to abort.", backupName), true);
        return SUCCESS;
    }


    /**
     * Backup with default name.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int backupWithDefaultName(CommandContext<ServerCommandSource> context) {
        return doBackup(context, "noname");
    }

    private static int doBackup(CommandContext<ServerCommandSource> context, String customName) {
        // Real backup name (compatible with legacy backup): date_name, such as 2020-04-23_21-03-00_test
        //KBMain.backup("name")
        String backupName = BackupNameTimeFormatter.getTimeString() + "_" + customName;

        // Validate file name
        final char[] ILLEGAL_CHARACTERS = {'/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
        for (char c : ILLEGAL_CHARACTERS) {
            if (backupName.contains(String.valueOf(c))) {
                msgErr(context, String.format("Name cannot contain special character \"%c\".", c));
                return FAILED;
            }
        }

        // Do backup
        BackupMetadata metadata = new BackupMetadata(System.currentTimeMillis(), backupName);
        PrintUtil.info("Invoking backup worker ...");
        BackupWorker.invoke(context, backupName, metadata);
        return SUCCESS;
    }

    /**
     * Restore with context parameter backupName.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int confirm(CommandContext<ServerCommandSource> context) {
        if (pendingOperation == null) {
            msgWarn(context, "Nothing to be confirmed. Please execute /kb restore <backup_name> first.");
            return FAILED;
        }

        AbstractConfirmableOperation operation = pendingOperation;
        pendingOperation = null;

        boolean returnValue = operation.confirm();

        // By the way, update suggestion list.
        BackupNameSuggestionProvider.updateCandidateList();

        return returnValue ? SUCCESS : FAILED; // block compiler's complain.
    }

    /**
     * Cancel the execution to be confirmed.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int cancel(CommandContext<ServerCommandSource> context) {
        if (pendingOperation != null) {
            PrintUtil.msgInfo(context, String.format("The %s has been cancelled.", pendingOperation.toString()), true);
            pendingOperation = null;
            return SUCCESS;
        } else {
            msgErr(context, "Nothing to cancel.");
            return FAILED;
        }
    }

    /**
     * Show the most recent backup.
     * If there is no available backup, print specific info.
     *
     * @param context the context.
     * @return stat code.
     */
    public static int prev(CommandContext<ServerCommandSource> context) {
        try {
            // List all backups
            MinecraftServer server = context.getSource().getMinecraftServer();
            List<File> files = Arrays.asList(Objects.requireNonNull(getBackupSaveDirectory(server).listFiles()));
            files.removeIf(f -> !f.getName().startsWith(BackupFilesystemUtil.getBackupFileNamePrefix()));
            files.sort((x, y) -> (int) (BackupFilesystemUtil.getBackupTimeFromBackupFileName(y.getName()) - BackupFilesystemUtil.getBackupTimeFromBackupFileName(x.getName())));
            File prevBackupFile = files.get(0);
            String backupName = getBackupName(prevBackupFile.getName());
            int i = backupNameList.indexOf(backupName);
            if (i == -1) {
                backupNameList.add(backupName);
                i = backupNameList.size();
            } else {
                ++i;
            }
            msgInfo(context, String.format("The most recent backup: [%d] %s , size: %s", i, backupName, humanFileSize(prevBackupFile.length())));
        } catch (NullPointerException e) {
            msgInfo(context, "There are no backups available.");
        } catch (SecurityException ignored) {
            msgErr(context, "Failed to read file.");
            return FAILED;
        }
        return SUCCESS;
    }


    private static String parseBackupName(CommandContext<ServerCommandSource> context, String userInput) {
        try {
            String backupName = StringArgumentType.getString(context, "backupName");

            if (backupName.matches("[0-9]*")) {
                // If numeric input
                int index = Integer.parseInt(backupName) - 1;
                return backupNameList.get(index); // Replace input number with real backup name.
            }
        } catch (NumberFormatException | IndexOutOfBoundsException ignored) {
        }
        return userInput;
    }
}
