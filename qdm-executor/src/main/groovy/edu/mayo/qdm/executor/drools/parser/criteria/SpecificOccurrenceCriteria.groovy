package edu.mayo.qdm.executor.drools.parser.criteria
import edu.mayo.qdm.executor.drools.parser.TemporalProcessor
import org.apache.commons.lang.BooleanUtils
import org.apache.commons.lang.StringUtils
import org.springframework.util.Assert
/**
 * An abstract base class for a Patient {@link Criteria}.
 */
class SpecificOccurrenceCriteria implements Criteria {
    def temporalProcessor = new TemporalProcessor()
    def valueProcessor = new ValueProcessor()

    def measureJson
    def json
    def measurementPeriod

    def retract = false

    def toDrools() {
        Assert.notNull(measurementPeriod, json.toString())

        def name = getName()
        def pluralName = getPluralName()

        def valueSetOid = json.value.code_list_id

        def references = temporalProcessor.processTemporalReferences(json.value.temporal_references, measurementPeriod, measureJson)

        def negation = BooleanUtils.toBoolean(json.value.negation)

        def negationCriteria = negation ? " negated == true " : ""

        def extraCriteria = this.getCriteria()

        def id = json.value.specific_occurrence
        def constant = json.value.specific_occurrence_const

        def eventCriteria = this.getEventCriteria()

        def hasEventCriteria = StringUtils.isNotBlank(eventCriteria)

        """
        ${references.variables}
        \$so: SpecificOccurrence(\$so_event :event, id == "$id", constant == "$constant", patient == \$p)

        ${(retract && !hasEventCriteria) ? " not " : "\$event : " }edu.mayo.qdm.patient.$name(
                        ${ [
                            //"this == \$so_event",
                            negationCriteria,
                            references.criteria,extraCriteria
                           ].findAll().join(",") }
        ) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}())
        ${(retract && hasEventCriteria) ? " not " : ""}${this.getEventCriteria()}
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

        if(retract){
            """
            System.out.println("RETRACTING!!!! ${json.key}");
            retract(\$so)
            """
        } else {
            """
            insertLogical(new PreconditionResult("${json.key}", \$p ${!negated ? ", \$event" : ""}))
            """
        }
    }
}
