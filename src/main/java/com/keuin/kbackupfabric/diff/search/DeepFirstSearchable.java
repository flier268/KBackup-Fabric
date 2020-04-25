package com.keuin.kbackupfabric.diff.search;

import java.util.function.Consumer;

/**
 * This interface provides an universal method to perform deep-first-search on certain data structure.
 * @param <NodeType> The node type. A node is a part of certain data structure containing its sub-nodes and sub-elements.
 */
public interface DeepFirstSearchable<NodeType> {

    /**
     * Traverse the tree in deep-first sequence, i.e. a deep-first-search (DFS) is performed.
     * @param consumer the consumer to process each nodes.
     */
    void dfs(Consumer<NodeType> consumer);
}
