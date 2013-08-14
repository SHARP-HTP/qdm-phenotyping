package edu.mayo.qdm.executor.cypress
import edu.mayo.qdm.cypress.CypressPatientDataSource
import edu.mayo.qdm.cypress.CypressValidator
import edu.mayo.qdm.executor.MeasurementPeriod
import edu.mayo.qdm.executor.drools.DroolsExecutor
import edu.mayo.qdm.executor.drools.cypress.AbstractAllCypressMeasuresTestIT
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
 * 24 failures
 */
//@Ignore
class CypressValidationTestIT extends AbstractAllCypressMeasuresTestIT {

    def resultsFile = new File("results.out")

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

        def patientList = cypressDataSource.getPatients()

        def results = this.executor.execute(patientList, xmlString, MeasurementPeriod.getCalendarYear(new DateTime(2012,1,1,1,1).toDate()))

        def xml = new XmlParser().parseText(xmlString)

        def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        def records     = builder.parse(new ByteArrayInputStream(xmlString.bytes)).documentElement

        def id = XPathFactory.newInstance().newXPath().evaluate( '//value[@root="2.16.840.1.113883.3.560.1"]/@extension', records, XPathConstants.STRING )

        def measureId = xml.id[0].@root

            cypressValidator.checkResults(measureId, results,
                {population, expected, actual, message ->
                    println message
                    assertEquals expected, actual, 0
                })

        FileUtils.write(resultsFile, """$id\n""", true)

    }

}

