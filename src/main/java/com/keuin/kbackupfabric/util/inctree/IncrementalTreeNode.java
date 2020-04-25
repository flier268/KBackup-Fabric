package com.keuin.kbackupfabric.util.inctree;

import java.util.Set;

public interface IncrementalTreeNode<EleType extends Versioned> {

    Set<IncrementalTreeNode<EleType>> subNodes();

    Set<EleType> subElements();

    String getName();

}
