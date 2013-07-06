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
                "procedure_performed":  { json -> [toDrools:{"//TODO"}] },
                "procedure_result":  { json -> [toDrools:{"//TODO"}] },
                "procedure_intolerance":  { json -> [toDrools:{"//TODO"}] },
                "risk_category_assessment":  { json -> [toDrools:{"//TODO"}] },
                "encounter": { json -> new Encounter(json:json, valueSetCodeResolver:valueSetCodeResolver) },
                "diagnosis_active": { json -> [toDrools:{"//TODO"}] },
                "diagnosis_inactive": { json -> [toDrools:{"//TODO"}] },
                "diagnosis_resolved": { json -> [toDrools:{"//TODO"}] },
                "laboratory_test": { json -> [toDrools:{"//TODO"}] },
                "physical_exam": { json -> [toDrools:{"//TODO"}] },
                "diagnostic_study_result": { json -> [toDrools:{"//TODO"}] },
                "diagnostic_study_performed": { json -> [toDrools:{"//TODO"}] },
                "medication_dispensed": { json -> [toDrools:{"//TODO"}] },
                "medication_active": { json -> [toDrools:{"//TODO"}] },
                "medication_administered": { json -> [toDrools:{"//TODO"}] },
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
