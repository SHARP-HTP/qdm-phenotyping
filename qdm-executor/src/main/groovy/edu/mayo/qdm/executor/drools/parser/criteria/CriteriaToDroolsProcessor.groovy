package edu.mayo.qdm.executor.drools.parser.criteria

import edu.mayo.qdm.executor.drools.parser.TemporalProcessor
import org.apache.commons.lang.BooleanUtils
import org.springframework.util.Assert

/**
 */
class CriteriaToDroolsProcessor {

    def temporalProcessor = new TemporalProcessor()
    def valueProcessor = new ValueProcessor()

    def toDrools(criteriaContext) {
        Assert.notNull(criteriaContext.measurementPeriod, criteriaContext.json.toString())

        def name = criteriaContext.name
        def pluralName = criteriaContext.pluralName

        def valueSetOid = criteriaContext.json.value.code_list_id

        def references =
            temporalProcessor.processTemporalReferences(
                    criteriaContext.json.value.temporal_references,
                    criteriaContext.measurementPeriod,
                    criteriaContext.measureJson)

        def negation = BooleanUtils.toBoolean(criteriaContext.json.value.negation)

        def negationCriteria = negation ? " negated == true " : ""

        def extraCriteria = criteriaContext.criteria
        """
        ${references.variables}
        \$event : edu.mayo.qdm.patient.$name(
                        ${ [negationCriteria,references.criteria,extraCriteria].findAll().join(",") }
        ) from droolsUtil.findMatches("$valueSetOid", \$p.get${pluralName}())
        ${criteriaContext.eventCriteria}
        """
    }

}
