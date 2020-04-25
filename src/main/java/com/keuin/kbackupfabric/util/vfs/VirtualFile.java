package com.keuin.kbackupfabric.util.vfs;

import com.keuin.kbackupfabric.diff.difftree.ModificationType;
import com.keuin.kbackupfabric.diff.inc.Versioned;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;

public class VirtualFile implements Versioned {
    private final String name;
    private final long lastModifiedTimeEpochMillis;
    private final long size;

    private VirtualFile(String name, long lastModifiedTimeEpochMillis, long size) {
        this.name = name;
        this.lastModifiedTimeEpochMillis = lastModifiedTimeEpochMillis;
        this.size = size;
    }

    /**
     * Create a VirtualFile from an existing File instance.
     * @param file the file.
     * @return A valid VirtualFile if success. If I/O or security error occurs, return null.
     */
    public static @Nullable
    VirtualFile fromFile(File file) {
        try {
            if(file.exists()){
                BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                long epoch = attributes.lastModifiedTime().toMillis();
                long size = attributes.size();
                String name = file.getName();
                return new VirtualFile(name,epoch,size);
            }
        } catch(UnsupportedOperationException | IOException | SecurityException | NullPointerException ignored){
        }
        return null;
    }

    /**
     * Create a VirtualFile from a valid ZipEntry.
     * @param zipEntry the zip entry.
     * @return A valid VirtualFile if success. If internal exception occurs, return null.
     */
    public static @Nullable
    VirtualFile fromZipEntry(ZipEntry zipEntry) {
        try {
            long epoch = zipEntry.getLastModifiedTime().toMillis();
            long size = zipEntry.getSize();
            String name = zipEntry.getName();
            return new VirtualFile(name,epoch,size);
        } catch(UnsupportedOperationException | SecurityException | NullPointerException ignored){
        }
        return null;
    }

    @Override
    public ModificationType compareWith(Versioned versionedObject) {
        if(versionedObject.getClass() != this.getClass())
            return ModificationType.INDIFFERENT; // Not virtual file

        VirtualFile file = (VirtualFile) versionedObject;

        if(!file.name.equals(this.name)) // If they are of the same name (considered the same file but differ in versions)
            return ModificationType.INDIFFERENT;

        if(this.lastModifiedTimeEpochMillis > file.lastModifiedTimeEpochMillis)
            return ModificationType.NEWER_THAN;
        if(this.lastModifiedTimeEpochMillis < file.lastModifiedTimeEpochMillis)
            return ModificationType.OLDER_THAN;

        // They have the same modified time. But we have to check the size in case.
        if(this.size == file.size)
            return ModificationType.IDENTICAL;
        else
            return ModificationType.MODIFIED;
    }
}
