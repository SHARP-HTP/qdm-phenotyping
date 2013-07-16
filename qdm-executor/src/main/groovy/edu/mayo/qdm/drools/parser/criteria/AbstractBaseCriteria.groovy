package edu.mayo.qdm.drools.parser.criteria

import edu.mayo.qdm.drools.parser.TemporalProcessor

/**
 * An abstract base class for a Patient {@link Criteria}.
 */
abstract class AbstractBaseCriteria implements Criteria {
    def temporalProcessor = new TemporalProcessor()

    def json
    def valueSetCodeResolver
    def measurementPeriod

    @Override
    def toDrools() {
        def name = getName()
        def pluralName = getPluralName()

        def valueSetOid = json.code_list_id

        def references = temporalProcessor.processTemporalReferences(json.temporal_references, measurementPeriod)

        """( )
        \$events : edu.mayo.qdm.patient.$name($references) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}())

        """
    }

    abstract def getName()

    def getPluralName(){
        getName() + "s"
    }

    @Override
    def hasEventList(){
        true
    }
}
