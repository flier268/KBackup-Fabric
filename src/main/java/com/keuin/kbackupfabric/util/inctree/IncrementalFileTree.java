package com.keuin.kbackupfabric.util.inctree;

import com.keuin.kbackupfabric.util.vfs.VirtualDirectory;
import com.keuin.kbackupfabric.util.vfs.VirtualFile;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class IncrementalFileTree implements IncrementalTree<VirtualDirectory, VirtualFile> {
    @Override
    public VirtualDirectory getRootNode() {
        return null;
    }

    @Override
    public Iterator<VirtualDirectory> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer action) {

    }

    @Override
    public Spliterator spliterator() {
        return null;
    }
}
