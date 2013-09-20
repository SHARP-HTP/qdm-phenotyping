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
                "allergy":  { Allergy },
                "communication": { Communication },
                "procedure_performed":  { Procedure },
                "procedure_result":  { Procedure },
                "procedure_intolerance":   { Procedure },
                "risk_category_assessment": { RiskCategoryAssessment },
                "encounter": { Encounter },
                "diagnosis_active": { Diagnosis },
                "diagnosis_inactive": { Diagnosis },
                "diagnosis_resolved": { Diagnosis },
                "laboratory_test": { Lab },
                "physical_exam": { PhysicalExamFinding },
                "diagnostic_study_result": { DiagnosticStudy },
                "diagnostic_study_performed": { DiagnosticStudy },
                "medication_dispensed": { Medication },
                "medication_active": { Medication },
                "medication_administered": { Medication },
                "medication_order": { Medication },
                "device_applied": { Procedure }
        ]

    def getCriteria(json, measureJson) {
        this.doGetCriteria(json, measureJson)
    }

    private def doGetCriteria(fullJson, measureJson) {
        def json = fullJson.value
        def qdsType = json.qds_data_type

        if(qdsType == "individual_characteristic") {
            if(json.property == "birthtime" || json.code_list_id == "2.16.840.1.113883.3.560.100.4"){
                return new Birthdate(fullJson)
            } else if (
                    json.property == null &&
                            json.definition.equals("patient_characteristic")){
                def criteria = new Characteristic(json:fullJson, measureJson: measureJson)

                return criteria
            } else {
                return new IndividualCharacteristic(fullJson)
            }
        }

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
                     \$context : context,
                     \$event : event,
                     patient == \$p)

                """
                def rhs =
                """
                insertLogical(new PreconditionResult("${fullJson.key}", \$p, \$event, \$context))
                """
                [
                    getLHS:{lhs},
                    getRHS:{rhs}
                ] as Criteria
            }
        } else {
            def criteriaFn = this.criteriaFactoryMap.get(qdsType)
            if (criteriaFn != null) {
                criteriaFn().newInstance([json:fullJson, measureJson: measureJson])
            } else {
                throw new RuntimeException("Critieria type: `$qdsType` not recognized. JSON -> $json")
            }
        }
    }

}
