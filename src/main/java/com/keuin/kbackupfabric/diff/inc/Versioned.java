package com.keuin.kbackupfabric.diff.inc;

import com.keuin.kbackupfabric.diff.difftree.ModificationType;

/**
 * Objects which have implemented this interface are of versions,
 * which means two file may have a new-old relation, and they
 * can be compared using newerThan method.
 */
public interface Versioned {

    /**
     * One object is newer than another,
     * when and only one is directly or indirectly modified from another.
     * If they are two totally indifferent files, this method should
     * return false.
     * @param versionedObject Another versioned object to compare with.
     * @return the comparision result.
     */
    ModificationType compareWith(Versioned versionedObject);
}
