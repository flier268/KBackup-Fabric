package com.keuin.kbackupfabric.diff.difftree;

import com.keuin.kbackupfabric.diff.inc.IncrementalTree;
import com.keuin.kbackupfabric.diff.inc.IncrementalTreeNode;
import com.keuin.kbackupfabric.diff.inc.Versioned;

public class Differ {

    public static <EleType extends Versioned> DiffTree<DiffNode<EleType>, EleType> diff(IncrementalTree<IncrementalTreeNode<EleType>, EleType> base, IncrementalTree<IncrementalTreeNode<EleType>, EleType> target) {

    }

}
