package edu.mayo.qdm.executor.drools.parser.criteria
import edu.mayo.qdm.executor.drools.parser.TemporalProcessor
import org.apache.commons.lang.BooleanUtils
import org.springframework.util.Assert
/**
 * An abstract base class for a Patient {@link Criteria}.
 */
abstract class AbstractBaseCriteria implements Criteria {
    def temporalProcessor = new TemporalProcessor()
    def valueProcessor = new ValueProcessor()

    def measureJson
    def json
    def valueSetCodeResolver
    def measurementPeriod

    def toDrools() {
        Assert.notNull(measurementPeriod, json.toString())

        def name = getName()
        def pluralName = getPluralName()

        def valueSetOid = json.value.code_list_id

        def references = temporalProcessor.processTemporalReferences(json.value.temporal_references, measurementPeriod, measureJson)

        def negation = BooleanUtils.toBoolean(json.value.negation)

        def negationCriteria = negation ? " negated == true " : ""

        def extraCriteria = this.getCriteria()
        """
        ${references.variables}
        \$event : edu.mayo.qdm.patient.$name(
                        ${ [negationCriteria,references.criteria,extraCriteria].findAll().join(",") }
        ) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}())
        ${this.getEventCriteria()}
        """
    }

    abstract def getName()

    def getCriteria() { "" }

    def getEventCriteria() { "" }

    def getPluralName(){
        getName() + "s"
    }

    @Override
    def getLHS(){
        """
        \$p : Patient ( )
        ${toDrools()}
        """
    }

    @Override
    def getRHS(){
        def negated = json.value.negation

        """
        insert(new PreconditionResult("${json.key}", \$p ${!negated ? ", \$event" : ""}))
        """
    }
}
