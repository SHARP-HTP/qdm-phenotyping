package edu.mayo.qdm.webapp.client.controller

import edu.mayo.qdm.cypress.CypressPatientDataSource
import edu.mayo.qdm.cypress.CypressValidator
import edu.mayo.qdm.executor.ExecutorFactory
import edu.mayo.qdm.executor.MeasurementPeriod
import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.ModelAndView
/**
 */
@Controller
class CypressValidationController implements InitializingBean {

    def resultsJson

    def cypressMeasureMap = [:]

    def validator = new CypressValidator()

    def cypressDataSource = new CypressPatientDataSource()

    def patientList = cypressDataSource.getPatients()

    @Override
    void afterPropertiesSet() throws Exception {
        resultsJson = new JsonSlurper().parseText(IOUtils.toString(new ClassPathResource("/cypress/results/by_measure.json").inputStream))

        def resolver = new PathMatchingResourcePatternResolver()

        def resources = resolver.getResources("classpath:/cypress/measures/ep/*/hqmf1.xml")

        resources.each {
            def xmlString = IOUtils.toString(it.inputStream, "UTF-8")

            def measureIdUuid = new XmlParser().parseText(xmlString).id[0].@root

            def nqfId = resultsJson.find {
                it.measure_id.equalsIgnoreCase(measureIdUuid)
            }.nqf_id

            cypressMeasureMap.put(nqfId,[uuid:measureIdUuid, xml:xmlString])
        }
    }

    @RequestMapping(value = "/executor/cypress/report", method = RequestMethod.GET)
    def getReportView(){
        new ModelAndView("executor/cypress/report", [measures:cypressMeasureMap.keySet()])
    }

    @RequestMapping(value = "/executor/cypress/measure/{hqmfId}/validation", method = RequestMethod.GET)
    @ResponseBody
    def validateCypressMeasure(@PathVariable String hqmfId){
        def measure = this.cypressMeasureMap.get(hqmfId)

        def executor = ExecutorFactory.instance().getExecutor()

        def results = executor.execute(patientList, measure.xml, MeasurementPeriod.getCalendarYear(new DateTime(2012,1,1,1,1).toDate()))

        def resultMap = [:]

        def resultCallback = {population, expected, actual, message ->
            if(expected == actual){
                resultMap.put(population.key, true)
            } else {
                resultMap.put(population.key, false)
            }
        }

        validator.checkResults(measure.uuid, results, resultCallback)


        resultMap
    }
}
