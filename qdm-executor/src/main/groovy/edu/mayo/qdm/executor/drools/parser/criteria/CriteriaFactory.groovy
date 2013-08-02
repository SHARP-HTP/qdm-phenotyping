package edu.mayo.qdm.executor.drools.parser.criteria

import edu.mayo.qdm.executor.valueset.ValueSetCodeResolver
import org.apache.commons.lang.BooleanUtils
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 */
@Component
class CriteriaFactory {

    @Resource
    ValueSetCodeResolver valueSetCodeResolver

    def TODO_CRITERIA = {json, measurementPeriod, measureJson ->
        [
                toDrools: {throw new UnsupportedOperationException("Unimplemented Criteria: `$json.qds_data_type`")},
                hasEventList:{false}
        ] as Criteria
    }

    def criteriaFactoryMap =
        [
                "individual_characteristic": { json, measurementPeriod, measureJson -> new IndividualCharacteristic(json, measurementPeriod) },
                "allergy": TODO_CRITERIA,
                "communication": TODO_CRITERIA,
                "procedure_performed":  { json, measurementPeriod, measureJson -> new Procedure(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "procedure_result": TODO_CRITERIA,
                "procedure_intolerance":  TODO_CRITERIA,
                "risk_category_assessment":  { json, measurementPeriod, measureJson -> new RiskCategoryAssessment(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "encounter": { json, measurementPeriod, measureJson -> new Encounter(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_active": { json, measurementPeriod, measureJson -> new Diagnosis(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_inactive": { json, measurementPeriod, measureJson -> new Diagnosis(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_resolved": { json, measurementPeriod, measureJson -> new Diagnosis(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "laboratory_test": { json, measurementPeriod, measureJson -> new Lab(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "physical_exam": { json, measurementPeriod, measureJson -> new PhysicalExamFinding(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnostic_study_result": { json, measurementPeriod, measureJson -> new Lab(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnostic_study_performed": TODO_CRITERIA,
                "medication_dispensed": { json, measurementPeriod, measureJson -> new Medication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_active": { json, measurementPeriod, measureJson -> new Medication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_administered": { json, measurementPeriod, measureJson -> new Medication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_order": { json, measurementPeriod, measureJson -> new Medication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "device_applied": TODO_CRITERIA
        ]

    def getCriteria(json, measurementPeriod, measureJson) {
        if(BooleanUtils.toBoolean(json.negation)){
            def criteria = this.doGetCriteria(json, measurementPeriod, measureJson)
            [
                toDrools:{"${criteria.toDrools()}"},
                hasEventList:{criteria.hasEventList()},
            ] as Criteria
        } else {
            this.doGetCriteria(json, measurementPeriod, measureJson)
        }
    }

    private def doGetCriteria(json, measurementPeriod, measureJson) {
        def qdsType = json.qds_data_type

        if(json.type.equals("derived")){
            def collections = []

            json.children_criteria.each {
                collections.add(it)
            }

            def criteria =
            """
                PreconditionResult(
                 (${collections.collect{"id == \"$it\""}.join(" || ")}),
                 \$event : event,
                 patient == \$p)

            """
            [
                toDrools:{criteria},
                hasEventList:{true},
            ] as Criteria
        } else {
            def criteriaFn = this.criteriaFactoryMap.get(qdsType)
            if (criteriaFn != null) {
                criteriaFn(json, measurementPeriod, measureJson)
            } else {
                throw new RuntimeException("Critieria type: `$qdsType` not recognized. JSON -> $json")
            }
        }
    }
}
