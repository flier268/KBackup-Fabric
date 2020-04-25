package com.keuin.kbackupfabric.util.naming;

import com.keuin.kbackupfabric.util.ReflectionUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import net.minecraft.world.World;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Functions deal with file name, directory name about Minecraft saves.
 * Hide the ugly method call into this class, keeping our code tidy.
 */
public final class BackupFilesystemUtil {

    private static final String backupSaveDirectoryName = "backups";
    private static final String backupFileNamePrefix = "kbackup-";

    /**
     * Get a full (not incremental) backup file name from its backupName.
     * @param backupName the backupName.
     * @return the backup file name.
     */
    public static String getBackupFileName(String backupName) {
        return backupFileNamePrefix + backupName + ".zip";
    }

    /**
     * Get backupName from backup file name
     * @param backupFileName the file name
     * @return the backupName
     */
    public static String getBackupName(String backupFileName) {
        try {
            if (backupFileName.matches(backupFileNamePrefix + ".+\\.zip"))
                return backupFileName.substring(backupFileNamePrefix.length(), backupFileName.length() - 4);
        } catch (IndexOutOfBoundsException ignored) {
        }
        return backupFileName;
    }

    /**
     * Check if the backup file of given backupName exists.
     * @param backupName the backupName.
     * @param server the MinecraftServer instance.
     * @return true if the file exists, false if the file does not exist.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isBackupNameValid(String backupName, MinecraftServer server) {
        File backupFile = new File(getBackupSaveDirectory(server), getBackupFileName(backupName));
        return backupFile.isFile();
    }

    /**
     * Get the File instance of backup save directory.
     * @param server the server.
     * @return the file instance.
     */
    public static File getBackupSaveDirectory(MinecraftServer server) {
        return new File(server.getRunDirectory(), backupSaveDirectoryName);
    }

    public static String getLevelPath(MinecraftServer server) {
        return (new File(server.getRunDirectory(), server.getLevelName())).getAbsolutePath();
    }

    public static String getWorldDirectoryName(World world) throws NoSuchFieldException, IllegalAccessException {
        File saveDir;
        ThreadedAnvilChunkStorage threadedAnvilChunkStorage = (ThreadedAnvilChunkStorage) ReflectionUtils.getPrivateField(world.getChunkManager(), "threadedAnvilChunkStorage");
        saveDir = (File) ReflectionUtils.getPrivateField(threadedAnvilChunkStorage, "saveDir");
        return saveDir.getName();
    }

    public static String getBackupFileNamePrefix() {
        return backupFileNamePrefix;
    }

    public static long getBackupTimeFromBackupFileName(String backupFileName) {
        Matcher matcher = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}_[0-9]{2}-[0-9]{2}-[0-9]{2}").matcher(backupFileName);
        if (matcher.find()) {
            String timeString = matcher.group(0);
            long timeStamp = BackupNameTimeFormatter.timeStringToEpochSeconds(timeString);
            System.out.println(backupFileName + " -> " + timeStamp);
            return timeStamp;
        }
        return -1;
    }

    public static String humanFileSize(long size) {
        double fileSize = size * 1.0 / 1024 / 1024; // Default unit is MB
        if (fileSize > 1000)
            //msgInfo(context, String.format("File size: %.2fGB", fileSize / 1024));
            return String.format("%.2fGB", fileSize / 1024);
        else
            //msgInfo(context, String.format("File size: %.2fMB", fileSize));
            return String.format("%.2fMB", fileSize);
    }
}
