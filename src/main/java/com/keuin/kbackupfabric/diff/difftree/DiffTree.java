package com.keuin.kbackupfabric.diff.difftree;

import com.keuin.kbackupfabric.diff.inc.Versioned;
import com.keuin.kbackupfabric.diff.search.DeepFirstSearchable;

public interface DiffTree<NodeType extends DiffNode<EleType>, EleType extends Versioned> extends DeepFirstSearchable<NodeType> {

    NodeType getRootNode();

}
