package edu.mayo.qdm.executor.drools.parser.occurrences

/**
 */
class Node {

    def id
    def parent
    def children = []
    def conjunction

    @Override
    public java.lang.String toString() {
        return "Node{" +
                "id=" + id +
                ", conjunction=" + conjunction +
                '}';
    }
}
