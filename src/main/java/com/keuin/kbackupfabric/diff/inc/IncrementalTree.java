package com.keuin.kbackupfabric.diff.inc;

import com.keuin.kbackupfabric.diff.search.DeepFirstSearchable;

public interface IncrementalTree<NodeType extends IncrementalTreeNode<EleType>, EleType extends Versioned> extends DeepFirstSearchable<NodeType> {

    NodeType getRootNode();

}
