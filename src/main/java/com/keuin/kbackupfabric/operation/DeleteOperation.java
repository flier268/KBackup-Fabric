package com.keuin.kbackupfabric.operation;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

import static com.keuin.kbackupfabric.util.BackupFilesystemUtil.getBackupFileName;
import static com.keuin.kbackupfabric.util.BackupFilesystemUtil.getBackupSaveDirectory;
import static com.keuin.kbackupfabric.util.PrintUtil.msgErr;
import static com.keuin.kbackupfabric.util.PrintUtil.msgInfo;
import static org.apache.commons.io.FileUtils.forceDelete;

class DeleteOperation extends Confirmable {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String backupName;
    private final CommandContext<ServerCommandSource> context;

    DeleteOperation(CommandContext<ServerCommandSource> context, String backupName) {
        this.backupName = backupName;
        this.context = context;
    }

    @Override
    public boolean confirm() {
        MinecraftServer server = context.getSource().getMinecraftServer();
        String backupFileName = getBackupFileName(backupName);
        LOGGER.info("Deleting backup " + backupName);
        File backupFile = new File(getBackupSaveDirectory(server), backupFileName);
        int tryCounter = 0;
        do {
            if (tryCounter == 5) {
                String msg = "Failed to delete file " + backupFileName;
                LOGGER.error(msg);
                msgErr(context, msg);
                return false;
            }
            try {
                if (!backupFile.delete())
                    forceDelete(backupFile);
            } catch (SecurityException | NullPointerException | IOException ignored) {
            }
            ++tryCounter;
        } while (backupFile.exists());
        LOGGER.info("Deleted backup " + backupName);
        msgInfo(context, "Deleted backup " + backupName);
        return true;
    }

    @Override
    public String toString() {
        return String.format("deletion of %s", backupName);
    }
}