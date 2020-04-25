package com.keuin.kbackupfabric.diff.difftree;

/**
 * The modification of a versioned object.
 */
public enum ModificationType {
    IDENTICAL, // It keeps all its attributes unchanged.
    MODIFIED, // It is modified.
    DELETED, // It is deleted.
    INDIFFERENT // The two compared object do not own a parent-child version relationship.
}
