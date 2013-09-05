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

    def references

    def toDrools() {
        Assert.notNull(measurementPeriod, json.toString())

        def name = getName()
        def pluralName = getPluralName()

        def valueSetOid = json.value.code_list_id

        this.references = temporalProcessor.processTemporalReferences(json.value.temporal_references, measurementPeriod, measureJson)

        def negation = BooleanUtils.toBoolean(json.value.negation)

        def negationCriteria = negation ? " negated == true " : ""

        def extraCriteria = this.getCriteria()
        """
        ${references.variables.collect { """\$$it : PreconditionResult(id == "$it", patient == \$p) """ }.join("\n")}
        \$event : edu.mayo.qdm.patient.$name(
                        ${ [negationCriteria,references.criteria,extraCriteria].findAll().join(",") }
        ) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}())
        ${this.getEventCriteria()}
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

        def so = getSpecificOccurrence(json.key, measureJson)

        def variables = references.variables

        def clause = ""
        if(so && variables){
            clause = """, droolsUtil.combine([${variables.collect{"""\$${it}.context"""}.join(",")}], new SpecificOccurrence("${so.constant}", "${so.id}", \$event))"""
        } else if(so){
            clause = """, new SpecificOccurrence("${so.constant}", "${so.id}", \$event)"""
        } else if(variables){
            clause = """, droolsUtil.combine([${variables.collect{"""\$${it}.context"""}.join(",")}])"""
        }

        """
        insertLogical(new PreconditionResult("${json.key}", \$p, ${!negated ? "\$event" : "null"} $clause))
        """
    }

    private class SpecificOccurrence{
        def id
        def constant
    }

    def getSpecificOccurrence(reference, measureJson){
        def criteria = measureJson.data_criteria.get(reference)
        if(criteria.specific_occurrence_const && criteria.specific_occurrence_const){
            new SpecificOccurrence(
                    id: criteria.specific_occurrence,
                    constant: criteria.specific_occurrence_const)
        }
    }
}
