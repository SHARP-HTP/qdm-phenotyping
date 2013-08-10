package edu.mayo.qdm.executor.drools.parser.criteria
import edu.mayo.qdm.executor.valueset.ValueSetCodeResolver
import org.springframework.stereotype.Component

import javax.annotation.Resource
/**
 */
@Component
class CriteriaFactory {

    def GroupOperatorFactory groupOperatorFactory = new GroupOperatorFactory()

    @Resource
    ValueSetCodeResolver valueSetCodeResolver

    def criteriaFactoryMap =
        [
                "individual_characteristic": {
                    json, measurementPeriod, measureJson ->
                        if(json.value.property == "birthtime"){
                            new Birthdate(json, measurementPeriod)
                        } else {
                            new IndividualCharacteristic(json, measurementPeriod)
                        }
                },
                "allergy":  { json, measurementPeriod, measureJson -> new Allergy(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "communication": { json, measurementPeriod, measureJson -> new Communication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "procedure_performed":  { json, measurementPeriod, measureJson -> new Procedure(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "procedure_result":  { json, measurementPeriod, measureJson -> new Procedure(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "procedure_intolerance":   { json, measurementPeriod, measureJson -> new Procedure(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "risk_category_assessment": { json, measurementPeriod, measureJson -> new RiskCategoryAssessment(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "encounter": { json, measurementPeriod, measureJson -> new Encounter(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_active": { json, measurementPeriod, measureJson -> new Diagnosis(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_inactive": { json, measurementPeriod, measureJson -> new Diagnosis(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnosis_resolved": { json, measurementPeriod, measureJson -> new Diagnosis(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "laboratory_test": { json, measurementPeriod, measureJson -> new Lab(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "physical_exam": { json, measurementPeriod, measureJson -> new PhysicalExamFinding(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnostic_study_result": { json, measurementPeriod, measureJson -> new DiagnosticStudy(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "diagnostic_study_performed": { json, measurementPeriod, measureJson -> new DiagnosticStudy(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_dispensed": { json, measurementPeriod, measureJson -> new Medication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_active": { json, measurementPeriod, measureJson -> new Medication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_administered": { json, measurementPeriod, measureJson -> new Medication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "medication_order": { json, measurementPeriod, measureJson -> new Medication(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) },
                "device_applied": { json, measurementPeriod, measureJson -> new Procedure(measureJson:measureJson, json:json, valueSetCodeResolver:valueSetCodeResolver, measurementPeriod:measurementPeriod) }
        ]

    def getCriteria(json, measurementPeriod, measureJson) {
        this.doGetCriteria(json, measurementPeriod, measureJson)
    }

    private def doGetCriteria(fullJson, measurementPeriod, measureJson) {
        def json = fullJson.value
        def qdsType = json.qds_data_type

        if(json.type.equals("derived")){
            def collections = []

            json.children_criteria.each {
                collections.add(it)
            }

            def subsets = json.subset_operators
            if(subsets){
                if(subsets.size() != 1){
                    throw new UnsupportedOperationException("Cannot process more than one Subset Operator per Group.\nJSON ->  $json")
                }

                def subsetOperator = subsets[0].type

                if(this.groupOperatorFactory.hasProperty(subsetOperator)){
                    return this.groupOperatorFactory."$subsetOperator"(fullJson, subsets[0])
                } else {
                    throw new RuntimeException("Subset Operator `$subsetOperator` not recognized.\nJSON ->  $json")
                }
            } else {
                def lhs =
                """
                    \$p : Patient ( )
                    PreconditionResult(
                     (${collections.collect{"id == \"$it\""}.join(" || ")}),
                     \$event : event,
                     patient == \$p)

                """
                def rhs =
                """
                insert(new PreconditionResult("${fullJson.key}", \$p, \$event))
                """
                [
                    getLHS:{lhs},
                    getRHS:{rhs}
                ] as Criteria
            }
        } else {
            def criteriaFn = this.criteriaFactoryMap.get(qdsType)
            if (criteriaFn != null) {
                criteriaFn(fullJson, measurementPeriod, measureJson)
            } else {
                throw new RuntimeException("Critieria type: `$qdsType` not recognized. JSON -> $json")
            }
        }
    }
}
