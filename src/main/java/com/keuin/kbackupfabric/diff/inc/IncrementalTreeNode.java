package com.keuin.kbackupfabric.diff.inc;

import java.util.Set;

public interface IncrementalTreeNode<EleType extends Versioned> {

    Set<IncrementalTreeNode<EleType>> subNodes();

    Set<EleType> subElements();

    String getName();

}
