package com.keuin.kbackupfabric.util.vfs;

import com.keuin.kbackupfabric.util.inctree.VersionComparison;
import com.keuin.kbackupfabric.util.inctree.Versioned;

public class VirtualFile implements Versioned {
    private String name;
    private long lastModifiedTimeEpoch;
    private long size;

    @Override
    public VersionComparison newerThan(Versioned versionedObject) {
        return null;
    }
}
