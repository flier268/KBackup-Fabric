package com.keuin.kbackupfabric.util.vfs;

import com.keuin.kbackupfabric.diff.inc.IncrementalTreeNode;

import java.util.*;

public class VirtualDirectory implements IncrementalTreeNode<VirtualFile> {

    private final String name;
    private final List<VirtualDirectory> subDirs = new ArrayList<>();
    private final List<VirtualFile> subFiles = new ArrayList<>();


    public VirtualDirectory(String name, Collection<VirtualDirectory> subDirs, Collection<VirtualFile> subFiles) {
        this.name = name;
        this.subDirs.addAll(subDirs);
        this.subFiles.addAll(subFiles);
    }

    @Override
    public Set<IncrementalTreeNode<VirtualFile>> subNodes() {
        return new HashSet<>(subDirs);
    }

    @Override
    public Set<VirtualFile> subElements() {
        return new HashSet<>(subFiles);
    }

    @Override
    public String getName() {
        return name;
    }
}
