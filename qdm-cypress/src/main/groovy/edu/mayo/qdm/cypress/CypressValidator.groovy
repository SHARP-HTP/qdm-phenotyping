package edu.mayo.qdm.cypress

import groovy.json.JsonSlurper
import org.springframework.core.io.ClassPathResource

/**
 */
class CypressValidator {

    def slurper = new JsonSlurper()

    def resultsJson =
        slurper.parse(new InputStreamReader(
                new ClassPathResource("/cypress/results/by_measure.json").getInputStream()))

    def checkResults(measureId, results, callback){
        def resultJson = resultsJson.find {
            ((it.measure_id == measureId.toUpperCase())
            &&
            (it.sub_id == null || it.sub_id == "a"))
        }

        resultJson.population_ids.each {
            def expected = resultJson[it.key]
            def actual = results.get(it.key).size()

            def message = "Criteria($it.key) - Expected: $expected, Actual: $actual, Found Patients: ${results.get(it.key).collect {"'"+it.sourcePid+"'"}}"

            callback(it, expected, actual, message)
        }
    }

}
