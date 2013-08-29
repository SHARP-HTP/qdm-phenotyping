package edu.mayo.qdm.executor.drools.parser.criteria
import edu.mayo.qdm.executor.drools.parser.TemporalProcessor
import org.apache.commons.lang.BooleanUtils
import org.springframework.util.Assert
/**
 * An abstract base class for a Patient {@link Criteria}.
 */
class DefaultCriteria implements Criteria {
    def temporalProcessor = new TemporalProcessor()
    def valueProcessor = new ValueProcessor()

    def measureJson
    def json
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
        ${
            if(references.variables){
                """
                ${references.variables}
                \$var : Event() from \$temporalReference.events
                """
            } else {
                ""
            }
        }

        \$events : Set() from collect(\$event : edu.mayo.qdm.patient.$name(
                        ${ [negationCriteria,references.criteria,extraCriteria,this.getEventCriteria()].findAll().join(",") }
        ) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}())
        )

        \$result : PreconditionResult(id == "${json.key}", patient == \$p)
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

        """
        System.out.println("SIZE: " + \$events.size());
        if(\$events.size() > 0){
            modify(\$result){
                events.addAll(\$events),
                status = PreconditionResultStatus.SUCCESS
            }
        } else if(\$events.size() == 0 && \$result.events.size() == 0){
            modify(\$result){
                status = PreconditionResultStatus.FAILURE
            }
        }


        //insertLogical(new PreconditionResult("${json.key}", \$p ${!negated ? ", \$events" : ""}))
        """
    }
}
