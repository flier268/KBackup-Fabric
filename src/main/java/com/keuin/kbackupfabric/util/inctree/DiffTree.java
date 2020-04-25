package com.keuin.kbackupfabric.util.inctree;

public interface DiffTree<NodeType extends DiffNode<EleType>, EleType extends Versioned> {

    NodeType getRootNode();

}
