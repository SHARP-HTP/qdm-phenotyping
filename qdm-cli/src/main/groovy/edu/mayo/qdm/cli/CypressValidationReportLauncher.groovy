package edu.mayo.qdm.cli
import edu.mayo.qdm.cypress.CypressPatientDataSource
import edu.mayo.qdm.cypress.CypressValidator
import edu.mayo.qdm.executor.ExecutorFactory
import edu.mayo.qdm.executor.MeasurementPeriod
import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
/**
 */
class CypressValidationReportLauncher extends AbstractBaseCliLauncher {

    public static void main(String[] args) throws IOException {
        new CypressValidationReportLauncher().doMain(args);
    }

    @Override
    protected void run() throws Exception {
        def cypressDataSource = new CypressPatientDataSource()

        def cypressValidator = new CypressValidator()

        def executor = ExecutorFactory.instance().getExecutor()

        System.out.println("""
================================
| Validating Cypress Test Data |
================================""")

        def resolver = new PathMatchingResourcePatternResolver()

        def resources = resolver.getResources("classpath:/cypress/measures/ep/*/hqmf1.xml")

        def successes = []
        def failures = []
        resources.each {
            def result = doExecute(it.inputStream, cypressDataSource, cypressValidator, executor)
            (result.success ? successes : failures) << result.nqfId
        }

        System.out.println("""
================================
| ******    Summary     ****** |
================================
Total: ${successes.size() + failures.size()}
Successful (${successes.size()}): ${successes.join(",")}
Failures (${failures.size()}): ${failures.join(",")}
""")


    }

    def doExecute(xmlStream, cypressDataSource, cypressValidator, executor) throws IOException {
        def xmlString = IOUtils.toString(xmlStream, "UTF-8")

        def patientList = cypressDataSource.getPatients()

        def results = executor.execute(patientList, xmlString, MeasurementPeriod.getCalendarYear(new DateTime(2012,1,1,1,1).toDate()))

        def measureIdUuid = new XmlParser().parseText(xmlString).id[0].@root

        def nqfId = new JsonSlurper().parseText(IOUtils.toString(new ClassPathResource("/cypress/results/by_measure.json").inputStream)).find {
            it.measure_id.equalsIgnoreCase(measureIdUuid)
        }.nqf_id

        def title = """| NQF ID: $nqfId |"""
        System.out.println("""\n${'='.multiply(title.size())}""")
        System.out.println(title)
        System.out.println("""${'='.multiply(title.size())}""")

        boolean success = true

        def systemOutCallback = {population, expected, actual, message ->
            if(expected == actual){
                System.out.println("[OK] " + message)
            } else {
                System.out.println("[FAILURE] " + message)
                success = false
            }
        }

        cypressValidator.checkResults(measureIdUuid, results, systemOutCallback)

        def result = """| Result: ${success ? "OK" : "FAILURE"} |"""
        System.out.println('='.multiply(result.size()))
        System.out.println(result)
        System.out.println('='.multiply(result.size()))

        [nqfId: nqfId, success: success]
    }
}
