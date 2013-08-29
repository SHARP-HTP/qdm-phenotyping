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
                "allergy":  { processor -> processor.metaClass.mixin Allergy; processor },
                "communication": { processor -> processor.metaClass.mixin Communication; processor },
                "procedure_performed":  { processor -> processor.metaClass.mixin Procedure; processor },
                "procedure_result":  { processor -> processor.metaClass.mixin Procedure; processor },
                "procedure_intolerance":   { processor -> processor.metaClass.mixin Procedure; processor },
                "risk_category_assessment": { processor -> processor.metaClass.mixin RiskCategoryAssessment; processor },
                "encounter": { processor -> processor.metaClass.mixin Encounter; processor },
                "diagnosis_active": { processor -> processor.metaClass.mixin Diagnosis; processor },
                "diagnosis_inactive": { processor -> processor.metaClass.mixin Diagnosis; processor },
                "diagnosis_resolved": { processor -> processor.metaClass.mixin Diagnosis; processor },
                "laboratory_test": { processor -> processor.metaClass.mixin Lab; processor },
                "physical_exam": { processor -> processor.metaClass.mixin PhysicalExamFinding; processor },
                "diagnostic_study_result": { processor -> processor.metaClass.mixin DiagnosticStudy; processor },
                "diagnostic_study_performed": { processor -> processor.metaClass.mixin DiagnosticStudy; processor },
                "medication_dispensed": { processor -> processor.metaClass.mixin Medication; processor },
                "medication_active": { processor -> processor.metaClass.mixin Medication; processor },
                "medication_administered": { processor -> processor.metaClass.mixin Medication; processor },
                "medication_order": { processor -> processor.metaClass.mixin Medication; processor },
                "device_applied": { processor -> processor.metaClass.mixin Procedure; processor }
        ]

    def getCriteria(json, measurementPeriod, measureJson) {
        this.doGetCriteria(json, measurementPeriod, measureJson)
    }

    def getSpecificOccurrenceDataCriteria(fullJson, measurementPeriod, measureJson) {
        def json = fullJson.value
        def qdsType = json.qds_data_type

        def criteriaFn = this.criteriaFactoryMap.get(qdsType)

        criteriaFn(new SpecificOccurrenceDataCriteria(json:fullJson, measurementPeriod: measurementPeriod, measureJson: measureJson))
    }

    private def doGetCriteria(fullJson, measurementPeriod, measureJson) {
        def json = fullJson.value
        def qdsType = json.qds_data_type

        if(qdsType == "individual_characteristic") {
            if(json.property == "birthtime"){
                return new Birthdate(fullJson, measurementPeriod)
            } else if (
                    json.property == null &&
                            json.definition.equals("patient_characteristic")){
                def criteria = new DefaultCriteria(json:fullJson, measurementPeriod: measurementPeriod, measureJson: measureJson)
                criteria.metaClass.mixin Characteristic

                return criteria
            } else {
                return new IndividualCharacteristic(fullJson, measurementPeriod)
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
                     \$events : events,
                     patient == \$p)

                """
                def rhs =
                """
                insertLogical(new PreconditionResult("${fullJson.key}", \$p, \$events))
                """
                [
                    getLHS:{lhs},
                    getRHS:{rhs}
                ] as Criteria
            }
        } else {
            def criteriaFn = this.criteriaFactoryMap.get(qdsType)
            if (criteriaFn != null) {
                criteriaFn(new DefaultCriteria(json:fullJson, measurementPeriod: measurementPeriod, measureJson: measureJson))
            } else {
                throw new RuntimeException("Critieria type: `$qdsType` not recognized. JSON -> $json")
            }
        }
    }

    def isSpecificOccurrence = {
        it.value.specific_occurrence && it.value.specific_occurrence_const
    }

    def isSpecificOccurrenceDataCriteria = {
        it.value.specific_occurrence &&
                it.value.specific_occurrence_const &&
                !it.value.temporal_references
    }

    def isSpecificOccurrenceCriteria = {
        it.value.specific_occurrence &&
                it.value.specific_occurrence_const &&
                it.value.temporal_references
    }
}
