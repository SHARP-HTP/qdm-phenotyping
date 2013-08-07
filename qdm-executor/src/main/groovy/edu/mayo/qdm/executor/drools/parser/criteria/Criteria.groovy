package edu.mayo.qdm.executor.drools.parser.criteria

/**
 * A Patient Criteria able to be converted into a Drools Rule (or a segment of a rule).
 */
interface Criteria {

    def getLHS()

    def getRHS()

}
