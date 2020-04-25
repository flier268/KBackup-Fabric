package com.keuin.kbackupfabric.util.vfs;

import com.keuin.kbackupfabric.util.inctree.IncrementalTreeNode;

import java.util.Set;

public class VirtualDirectory implements IncrementalTreeNode<VirtualFile> {
    @Override
    public Set<IncrementalTreeNode<VirtualFile>> subNodes() {
        return null;
    }

    @Override
    public Set<VirtualFile> subElements() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
