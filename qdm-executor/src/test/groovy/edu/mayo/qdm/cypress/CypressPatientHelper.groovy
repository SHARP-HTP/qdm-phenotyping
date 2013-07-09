package edu.mayo.qdm.cypress
import edu.mayo.qdm.Results
import edu.mayo.qdm.patient.*
import groovy.json.JsonSlurper
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
/**
 */
class CypressPatientHelper {

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
            case "F": gender = Gender.MALE; break
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
            patient.addProcedure(
                new Procedure(new Concept(code.value[0], code.key, null), toDate(procedure.start_time), toDate(procedure.end_time)))
        }

        json.medications.each { medication ->
            def code = medication.codes.iterator().next()
            patient.addMedication(
                new Medication(new Concept(code.value[0], code.key, null), toDate(medication.start_time), toDate(medication.end_time)))
        }

        json.conditions.each { condition ->
            def code = condition.codes.iterator().next()
            patient.addDiagnosis(
                new Diagnosis(new Concept(code.value[0], code.key, null), toDate(condition.start_time), toDate(condition.end_time)))
        }

        patient
    }

    def checkResults(measureId, Results results){
        def resultsJson =
            slurper.parse(new InputStreamReader(
                new ClassPathResource("/cypress/results/by_measure.json").getInputStream()))

        def resultJson = resultsJson.find { it.nqf_id == measureId}

        println """NQF ID: $measureId"""
        resultJson.population_ids.each {
            def expected = resultJson[it.key]
            def actual = results.get(it.key).size()

            println "Criteria($it.key) - Expected: $expected, Actual: $actual, Found Patients: ${results.get(it.key).collect {it.sourcePid}}"

            //assertEquals expected, actual, 0
        }
    }

    def toDate(time){
        time == null ? null : new Date(((long)time) * 1000)
    }

    static void main(String... args){
        print new CypressPatientHelper().getPatients()
    }
}
