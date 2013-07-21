package edu.mayo.qdm.executor.drools.parser.criteria

import edu.mayo.qdm.executor.drools.parser.TemporalProcessor
import org.springframework.util.Assert

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
        Assert.notNull(measurementPeriod, json.toString())

        def name = getName()
        def pluralName = getPluralName()

        def valueSetOid = json.code_list_id

        def references = temporalProcessor.processTemporalReferences(json.temporal_references, measurementPeriod)

        """( )
        \$events : Set( size > 0 ) from collect( edu.mayo.qdm.patient.$name($references) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}()))
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
