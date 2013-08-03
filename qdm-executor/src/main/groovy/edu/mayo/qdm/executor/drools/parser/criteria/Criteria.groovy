package edu.mayo.qdm.executor.drools.parser.criteria

/**
 * A Patient Criteria able to be converted into a Drools Rule (or a segment of a rule).
 */
interface Criteria {

    /**
     * Get the Drools representation of this Criteria
     *
     * @return the String Drools representation
     */
    def toDrools()

    /**
     * True if this Criteria is made up of a series of {@link edu.mayo.qdm.patient.Event}s
     *
     * @return true/false
     */
    def hasEventList()

    def isPatientCriteria()

}
