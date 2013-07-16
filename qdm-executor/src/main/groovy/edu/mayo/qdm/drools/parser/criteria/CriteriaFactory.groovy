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
                "individual_characteristic": { json, measurementPeriod -> new IndividualCharacteristic(json, measurementPeriod) },
                "allergy":  { json, measurementPeriod -> [toDrools:{throw new UnsupportedOperationException()}] },
                "communication":  { json, measurementPeriod -> [toDrools:{throw new UnsupportedOperationException()}] },
                "procedure_performed":  { json, measurementPeriod -> new Procedure(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "procedure_result":  { json, measurementPeriod -> [toDrools:{throw new UnsupportedOperationException()}] },
                "procedure_intolerance":  { json, measurementPeriod -> [toDrools:{throw new UnsupportedOperationException()}] },
                "risk_category_assessment":  { json, measurementPeriod -> new RiskCategoryAssessment(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "encounter": { json, measurementPeriod -> new Encounter(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_active": { json, measurementPeriod -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_inactive": { json, measurementPeriod -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_resolved": { json, measurementPeriod -> new Diagnosis(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "laboratory_test": { json, measurementPeriod -> new Lab(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "physical_exam": { json, measurementPeriod -> new PhysicalExamFinding(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "diagnostic_study_result": { json, measurementPeriod -> [toDrools:{throw new UnsupportedOperationException()}] },
                "diagnostic_study_performed": { json, measurementPeriod -> [toDrools:{throw new UnsupportedOperationException()}] },
                "medication_dispensed": { json, measurementPeriod -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_active": { json, measurementPeriod -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "medication_administered": { json, measurementPeriod -> new Medication(json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "device_applied": { json, measurementPeriod -> [toDrools:{throw new UnsupportedOperationException()}] },
                "medication_order": { json, measurementPeriod -> [toDrools:{throw new UnsupportedOperationException()}] }
        ]

    def getCriteria(json, measurementPeriod) {
        if(BooleanUtils.toBoolean(json.negation)){
            [toDrools:{"not( ${this.doGetCriteria(json, measurementPeriod).toDrools()} )"}]
        } else {
            this.doGetCriteria(json, measurementPeriod)
        }
    }

    private def doGetCriteria(json, measurementPeriod) {
        def qdsType = json.qds_data_type

        if(json.type.equals("derived")){
            [ toDrools:{ p -> throw new UnsupportedOperationException()} ]
        } else {
            def criteriaFn = this.criteriaFactoryMap.get(qdsType)
            if (criteriaFn != null) {
                criteriaFn(json, measurementPeriod)
            } else {
                throw new RuntimeException("Critieria type: `$qdsType` not recognized. JSON -> $json")
            }
        }
    }
}
