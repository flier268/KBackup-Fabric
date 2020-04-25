package com.keuin.kbackupfabric.diff.difftree;

import com.keuin.kbackupfabric.diff.inc.IncrementalTreeNode;
import com.keuin.kbackupfabric.diff.inc.Versioned;

import java.util.Set;

public interface DiffNode<EleType extends Versioned> {

    Set<IncrementalTreeNode<EleType>> subNodes();

    Set<EleType> subElements();

    String getName();

}
