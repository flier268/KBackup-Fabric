package com.keuin.kbackupfabric.util.inctree;

import java.util.Iterator;

public interface IncrementalTree<NodeType extends IncrementalTreeNode<EleType>, EleType extends Versioned> extends Iterable<NodeType> {

    NodeType getRootNode();

    @Override
    public Iterator<NodeType> iterator();

}
