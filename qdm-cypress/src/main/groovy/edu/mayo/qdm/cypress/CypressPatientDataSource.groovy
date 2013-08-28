package edu.mayo.qdm.cypress
import edu.mayo.qdm.patient.*
import groovy.json.JsonSlurper
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
/**
 */
class CypressPatientDataSource {

    def slurper = new JsonSlurper()

    def getPatients(){
        def patients = []
        def resolver = new PathMatchingResourcePatternResolver()
        resolver.getResources("classpath:/cypress/patients/ep/*.json").each {
            patients.add(transform(slurper.parse(new InputStreamReader(it.getInputStream()))))
        }

        patients
    }

    def transform = {json ->
        def patient = new Patient(json.last + ", " + json.first)
        patient.setBirthdate(toDate(json.birthdate))

        def gender
        switch (json.gender){
            case "M": gender = Gender.MALE; break
            case "F": gender = Gender.FEMALE; break
            defaut: gender = Gender.UNKNOWN
        }
        patient.setSex(gender)

        json.encounters.each { encounter ->
            def code = encounter.codes.iterator().next()
            patient.addEncounter(
                new Encounter("", new Concept(code.value[0], code.key, null), toDate(encounter.start_time), toDate(encounter.end_time)))
        }

        json.procedures.each { procedure ->
            def code = procedure.codes.iterator().next()
            def oid = procedure.oid

            if(oid == "2.16.840.1.113883.3.560.1.11" ||
                oid == "2.16.840.1.113883.3.560.1.40"){
                def study = new DiagnosticStudy(
                        new Concept(code.value[0], code.key, null),
                        toDate(procedure.start_time),
                        toDate(procedure.end_time))

                procedure.values?.each {
                    if(it._type == "CodedResultValue"){
                        def resultCode = it.codes.iterator().next()
                        study.results.add(new Concept(resultCode.value[0], resultCode.key, null))
                    } else if(it._type == "PhysicalQuantityResultValue"){
                        def value = procedure.values?.iterator()?.next()
                        if(it.scalar != null){
                            study.values.add(new Value(it.scalar, it.unit))
                        }
                    } else {
                        throw new UnsupportedOperationException("""Type `${it._type}` not recognized.""")
                    }
                }

                patient.addDiagnosticStudy(study)

            } else if(oid == "2.16.840.1.113883.3.560.1.29"){
                patient.addCommunication(
                        new Communication(new Concept(code.value[0], code.key, null), toDate(procedure.start_time), toDate(procedure.end_time)))
            } else if(oid == "2.16.840.1.113883.3.560.1.21"){
                patient.addRiskCategoryAssessment(
                        new RiskCategoryAssessment(new Concept(code.value[0], code.key, null), toDate(procedure.start_time), toDate(procedure.end_time)))
            } else if(
                    oid == "2.16.840.1.113883.3.560.1.57"
                    ||
                    oid == "2.16.840.1.113883.3.560.1.18"){
                def value = procedure.values?.iterator()?.next()
                def val = value?.scalar != null ? new Value(value.scalar, value.unit) : null;

                patient.addPhysicalExamFinding(
                    new PhysicalExamFinding(
                        new Concept(code.value[0], code.key, null),
                            val,
                            toDate(procedure.start_time),
                            toDate(procedure.end_time)))
            } else {
                def status = this.toProcedureStatus(procedure."status_code")

                patient.addProcedure(
                    new Procedure(
                            new Concept(code.value[0], code.key, null),
                            status,
                            toDate(procedure.start_time),
                            toDate(procedure.end_time)))
            }
        }

        json.medications.each { medication ->
            def code = medication.codes.iterator().next()
            def status = this.toMedicationStatus(medication."status_code")
            patient.addMedication(
                new Medication(new Concept(code.value[0], code.key, null), status, toDate(medication.start_time), toDate(medication.end_time)))
        }

        json.conditions.each { condition ->
            def oid = condition.oid

            def code = condition.codes.iterator().next()
            if(oid == "2.16.840.1.113883.3.560.1.1001"){
                patient.addCharacteristic(
                        new Characteristic(new Concept(code.value[0], code.key, null), toDate(condition.start_time), toDate(condition.end_time)))
            } else {
                patient.addDiagnosis(
                        new Diagnosis(new Concept(code.value[0], code.key, null), toDate(condition.start_time), toDate(condition.end_time)))
            }
        }

        json.vital_signs.each { vital_sign ->
            def code = vital_sign.codes.iterator().next()
            def value = vital_sign.values?.iterator()?.next()

            def val = value != null ? new Value(value.scalar, value.unit) : null;

            patient.addLab(
                    new Lab(
                        new Concept(code.value[0], code.key, null),
                        val,
                        toDate(vital_sign.start_time),
                        toDate(vital_sign.end_time)))
        }

        patient
    }

    def toMedicationStatus(statusJson){
        def status = statusJson."HL7 ActStatus"[0]

        switch (status){
            case "active": return MedicationStatus.ACTIVE
            case "administered": return MedicationStatus.ADMINISTERED
            case "dispensed": return MedicationStatus.DISPENSED
            case "ordered": return MedicationStatus.ORDERED
            default : throw new RuntimeException("""Status: $status not recognized.""")
        }
    }

    def toProcedureStatus(statusJson){
        def status = statusJson."HL7 ActStatus"[0]

        switch (status){
            case "performed": return ProcedureStatus.PERFORMED
            case "active": return ProcedureStatus.ACTIVE
            case "ordered": return ProcedureStatus.ORDERED
            case null: return null
            default : throw new RuntimeException("""Status: $status not recognized. JSON -> $statusJson""")
        }
    }

    def toDate(time){
        time == null ? null : new Date(((long)time) * 1000)
    }

}
