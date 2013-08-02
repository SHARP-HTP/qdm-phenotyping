package edu.mayo.qdm.executor.cypress

import edu.mayo.qdm.cypress.CypressPatientDataSource
import edu.mayo.qdm.cypress.CypressValidator
import edu.mayo.qdm.executor.MeasurementPeriod
import edu.mayo.qdm.executor.drools.DroolsExecutor
import edu.mayo.qdm.executor.drools.cypress.AbstractAllCypressMeasuresTestIT
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.junit.Ignore
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.Assert.assertEquals
/**
 */
@Ignore
class CypressValidationTestIT extends AbstractAllCypressMeasuresTestIT {

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

        def measureId = new XmlParser().parseText(xmlString).id[0].@root

        cypressValidator.checkResults(measureId, results,
                {population, expected, actual, message ->
                    println message
                    assertEquals expected, actual, 0
                })
    }

}

