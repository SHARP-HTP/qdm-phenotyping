package edu.mayo.qdm.executor.drools.parser.occurrences

import groovy.transform.EqualsAndHashCode

/**
 */
@EqualsAndHashCode
class Node {

    def id
    def parent
    def children = []
    def conjunction
    def negated = false
    def isLeaf = false

    @Override
    public java.lang.String toString() {
        return "Node{" +
                "id=" + id +
                ", conjunction=" + conjunction +
                '}';
    }
}
