package edu.mayo.qdm.drools.parser.criteria

import edu.mayo.qdm.valueset.ValueSetCodeResolver
import org.apache.commons.lang.BooleanUtils
import org.springframework.stereotype.Component

import javax.annotation.Resource

/**
 */
@Component
class CriteriaFactory {

    @Resource
    ValueSetCodeResolver valueSetCodeResolver

    def TODO_CRITERIA = {json, measurementPeriod ->
        [
                toDrools:{"(/* Unimplemented Criteria: `$json.qds_data_type` -- TODO */ eval(true) )"},
                hasEventList:{false}
        ] as Criteria
    }

    def criteriaFactoryMap =
        [
                "individual_characteristic": { json, measurementPeriod -> new IndividualCharacteristic(json, measurementPeriod) },
                "allergy": TODO_CRITERIA,
                "communication": TODO_CRITERIA,
                "procedure_performed":  { json, measurementPeriod -> new Procedure(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "procedure_result": TODO_CRITERIA,
                "procedure_intolerance":  TODO_CRITERIA,
                "risk_category_assessment":  { json, measurementPeriod -> new RiskCategoryAssessment(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "encounter": { json, measurementPeriod -> new Encounter(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_active": { json, measurementPeriod -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_inactive": { json, measurementPeriod -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_resolved": { json, measurementPeriod -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "laboratory_test": { json, measurementPeriod -> new Lab(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "physical_exam": { json, measurementPeriod -> new PhysicalExamFinding(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnostic_study_result": { json, measurementPeriod -> new Lab(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnostic_study_performed": TODO_CRITERIA,
                "medication_dispensed": { json, measurementPeriod -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_active": { json, measurementPeriod -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_administered": { json, measurementPeriod -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "device_applied": TODO_CRITERIA,
                "medication_order": TODO_CRITERIA
        ]

    def getCriteria(json, measurementPeriod) {
        if(BooleanUtils.toBoolean(json.negation)){
            def criteria = this.doGetCriteria(json, measurementPeriod)
            [
                toDrools:{"() not( ${criteria.toDrools()} )"},
                hasEventList:{criteria.hasEventList()},
            ] as Criteria
        } else {
            this.doGetCriteria(json, measurementPeriod)
        }
    }

    private def doGetCriteria(json, measurementPeriod) {
        def qdsType = json.qds_data_type

        if(json.type.equals("derived")){
            [
                toDrools:{"()/* Derived -- TODO */"},
                hasEventList:{false},
            ] as Criteria
        } else {
            def criteriaFn = this.criteriaFactoryMap.get(qdsType)
            if (criteriaFn != null) {
                criteriaFn(json, measurementPeriod)
            } else {
                print criteriaFactoryMap
                throw new RuntimeException("Critieria type: `$qdsType` not recognized. JSON -> $json")
            }
        }
    }
}
