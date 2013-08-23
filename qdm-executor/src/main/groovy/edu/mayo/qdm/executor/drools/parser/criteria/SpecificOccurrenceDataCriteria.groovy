package edu.mayo.qdm.executor.drools.parser.criteria
import edu.mayo.qdm.executor.drools.parser.TemporalProcessor
import org.apache.commons.lang.BooleanUtils
import org.springframework.util.Assert
/**
 * An abstract base class for a Patient {@link Criteria}.
 */
class SpecificOccurrenceDataCriteria implements Criteria {
    def temporalProcessor = new TemporalProcessor()
    def valueProcessor = new ValueProcessor()

    def measureJson
    def json
    def measurementPeriod

    def toDrools() {
        Assert.isNull(json.value.temporal_references)

        def name = getName()
        def pluralName = getPluralName()

        def valueSetOid = json.value.code_list_id

        def negation = BooleanUtils.toBoolean(json.value.negation)

        def negationCriteria = negation ? " negated == true " : ""

        def extraCriteria = this.getCriteria()
        """
        \$event : edu.mayo.qdm.patient.$name(
                        ${ [
                            negationCriteria,
                            extraCriteria
                           ].findAll().join(",") }
        ) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}())
        """
    }

    def getName(){ "" }

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

        def id = json.value.specific_occurrence
        def constant = json.value.specific_occurrence_const
        """
        /* LOOK HERE */
        //insert( new PreconditionResult("${json.key}", \$p ${!negated ? ", \$event" : ""}))
        insert( new SpecificOccurrence(\$event, "$id", "$constant", \$p))
        """
    }
}
