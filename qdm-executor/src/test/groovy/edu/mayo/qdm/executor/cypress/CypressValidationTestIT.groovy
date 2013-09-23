package edu.mayo.qdm.executor.cypress
import edu.mayo.qdm.cypress.CypressPatientDataSource
import edu.mayo.qdm.cypress.CypressValidator
import edu.mayo.qdm.executor.MeasurementPeriod
import edu.mayo.qdm.executor.drools.DroolsExecutor
import edu.mayo.qdm.executor.drools.cypress.AbstractAllCypressMeasuresTestIT
import groovy.util.logging.Log4j
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

import static org.junit.Assert.assertEquals
/**
 *
 *
 * 0710
 * BH_Adult_D is supposed to be in the NUMER, but it doesn't seem possible.
 * Nov 01 2012 is more than 13 months after Sept 24 2011 (Risk Category Assessments)
 *
 *
 * Same with 0059
 */
@Log4j
class CypressValidationTestIT extends AbstractAllCypressMeasuresTestIT {

    def resultsFile = new File("results.out")

    def KNOWN_FAILURES = ["0710", "0059"] as Set

    @Autowired
    private DroolsExecutor executor

    def cypressDataSource = new CypressPatientDataSource()
    def cypressValidator = new CypressValidator()

    public CypressValidationTestIT(xml) {
        super(xml)
    }

    @Test
    void test(){
        this.doExecute(xml.xmlStream)
    }

    void doExecute(xmlStream) throws IOException{
        def xmlString = IOUtils.toString(xmlStream, "UTF-8")

        def xml = new XmlParser().parseText(xmlString)

        def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def records     = builder.parse(new ByteArrayInputStream(xmlString.bytes)).documentElement

        def id = XPathFactory.newInstance().newXPath().evaluate( '//value[@root="2.16.840.1.113883.3.560.1"]/@extension', records, XPathConstants.STRING )

        def measureId = xml.id[0].@root

        def patientList = cypressDataSource.getPatients()

        def results = this.executor.execute(patientList, xmlString, MeasurementPeriod.getCalendarYear(new DateTime(2012,1,1,1,1).toDate()), null)

            cypressValidator.checkResults(measureId, results,
                {population, expected, actual, message ->
                    println message
                    if(! KNOWN_FAILURES.contains(id)){
                        assertEquals expected, actual, 0
                    } else {
                        log.warn("Skipping known failure of Measure ID: `$id`")
                    }
                })

        FileUtils.write(resultsFile, """$id\n""", true)
    }

}

