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

    def references

    def toDrools() {
        Assert.notNull(json.toString())

        def name = getName()
        def pluralName = getPluralName()

        def valueSetOid = json.value.code_list_id

        this.references = temporalProcessor.processTemporalReferences(json.value.temporal_references, measureJson)

        def negation = BooleanUtils.toBoolean(json.value.negation)

        def negationCriteria = negation ? " negated == true " : ""

        def extraCriteria = this.getCriteria()

        def subsetCriteria

        def subsets = json.value.subset_operators
        if(subsets){
            if(subsets.size() != 1){
                throw new UnsupportedOperationException("Cannot process more than one Subset Operator per Group.\nJSON ->  $json")
            }

            def subsetOperator = subsets[0].type

            if(this.hasProperty(subsetOperator)){
                subsetCriteria = this."$subsetOperator"(subsets[0])
            } else {
                throw new RuntimeException("Subset Operator `$subsetOperator` not recognized.\nJSON ->  $json")
            }
        }

        """
        ${references.variables.collect { """\$$it : PreconditionResult(id == "$it", patient == \$p) """ }.join("\n")}
        \$event${subsetCriteria ? "s" : ""} : ${subsetCriteria ? "Set() from collect(" : ""}edu.mayo.qdm.patient.$name(
                        ${ [negationCriteria,references.criteria,extraCriteria].findAll().join(",") }
        ) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}())
        ${subsetCriteria ? ")" : ""}

        ${subsetCriteria ? subsetCriteria : ""}
        ${this.getEventCriteria()}
        """
    }

    def RECENT = { subset ->
        recentOrFirst(subset, "max")
    }

    def FIRST = { subset ->
        recentOrFirst(subset, "min")
    }

    def recentOrFirst = { subset, minOrMax ->
        """
        \$m : Number() from accumulate(
                ${getName()}(
                        startDate != null,
                        \$startDate : startDate ) from \$events,
                $minOrMax( \$startDate.time ) )

        \$event : ${getName()}(
                startDate == new java.util.Date(\$m) ) from \$events
        """
    }

    def COUNT = { subset ->

            def low = subset.value.low
            def high = subset.value.high

            def countCriteria = []
            if(low){
                countCriteria << """\$events.size >${low."inclusive?" ? "=" : ""} ${low.value}"""
            }
            if(high){
                countCriteria << """\$events.size <${low."inclusive?" ? "=" : ""} ${high.value}"""
            }
            """
            ${
            countCriteria.collect { """
                                    eval($it)"""}.join()
            }
            \$event : Event() from \$events
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

    private static class SpecificOccurrence{
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
