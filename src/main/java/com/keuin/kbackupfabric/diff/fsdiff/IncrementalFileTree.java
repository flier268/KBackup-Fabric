package com.keuin.kbackupfabric.diff.fsdiff;

import com.keuin.kbackupfabric.diff.inc.IncrementalTree;
import com.keuin.kbackupfabric.util.vfs.VirtualDirectory;
import com.keuin.kbackupfabric.util.vfs.VirtualFile;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.function.Consumer;

public class IncrementalFileTree implements IncrementalTree<VirtualDirectory, VirtualFile> {

    private final VirtualDirectory root;

    private IncrementalFileTree(VirtualDirectory root) {
        this.root = root;
    }

    public static IncrementalFileTree fromFile(File rootFile) {
        try {
            Queue<File> queue = new LinkedList<>();
            Map<URI,File> map = new HashMap<>();
            queue.add(rootFile);
            while(!queue.isEmpty()) {
                File file = queue.remove();
                assert file.isDirectory();
                map.put(file.toURI(),file);

            }
        } catch(SecurityException ignored) {
        }
        return null;
    }

    @Override
    public VirtualDirectory getRootNode() {
        return null;
    }

    @Override
    public void dfs(Consumer<VirtualDirectory> consumer) {

    }
}
