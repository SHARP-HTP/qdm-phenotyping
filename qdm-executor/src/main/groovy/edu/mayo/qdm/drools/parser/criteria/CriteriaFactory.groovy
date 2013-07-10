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

    def criteriaFactoryMap =
        [
                "individual_characteristic": { json -> new IndividualCharacteristic(json) },
                "allergy":  { json -> [toDrools:{"//TODO"}] },
                "communication":  { json -> [toDrools:{"//TODO"}] },
                "procedure_performed":  { json -> new Procedure(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "procedure_result":  { json -> [toDrools:{"//TODO"}] },
                "procedure_intolerance":  { json -> [toDrools:{"//TODO"}] },
                "risk_category_assessment":  { json -> new RiskCategoryAssessment(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "encounter": { json -> new Encounter(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "diagnosis_active": { json -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "diagnosis_inactive": { json -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "diagnosis_resolved": { json -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "laboratory_test": { json -> [toDrools:{"//TODO"}] },
                "physical_exam": { json -> new PhysicalExamFinding(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "diagnostic_study_result": { json -> [toDrools:{"//TODO"}] },
                "diagnostic_study_performed": { json -> [toDrools:{"//TODO"}] },
                "medication_dispensed": { json -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "medication_active": { json -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "medication_administered": { json -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "device_applied": { json -> [toDrools:{"//TODO"}] },
                "medication_order": { json -> [toDrools:{"//TODO"}] }
        ]

    def getCriteria(json) {
        if(BooleanUtils.toBoolean(json.negation)){
            [toDrools:{"not( ${this.doGetCriteria(json).toDrools()} )"}]
        } else {
            this.doGetCriteria(json)
        }
    }

    private def doGetCriteria(json) {
        def qdsType = json.qds_data_type

        if(json.type.equals("derived")){
            [ toDrools:{ p -> "/*TODO: Derived (Grouping)*/"} ]
        } else {
            def criteriaFn = this.criteriaFactoryMap.get(qdsType)
            if (criteriaFn != null) {
                criteriaFn(json)
            } else {
                throw new RuntimeException("Critieria type: `$qdsType` not recognized. JSON -> $json")
            }
        }
    }
}
