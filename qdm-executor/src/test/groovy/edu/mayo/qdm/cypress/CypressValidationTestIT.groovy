package edu.mayo.qdm.cypress
import edu.mayo.qdm.executor.MeasurementPeriod
import edu.mayo.qdm.executor.drools.DroolsExecutor
import edu.mayo.qdm.executor.drools.cypress.AbstractAllCypressMeasuresTestIT
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

import static org.junit.Assert.assertEquals

/**
 */
class CypressValidationTestIT extends AbstractAllCypressMeasuresTestIT {

    @Autowired
    private DroolsExecutor executor

    def cypressHelper = new CypressPatientHelper()

    public CypressValidationTestIT(xml) {
        super(xml)
    }

    @Test
    void test(){
        this.doExecute(xml.xmlStream)
    }

    void doExecute(xmlStream) throws IOException{
        def xmlString = IOUtils.toString(xmlStream, "UTF-8")

        def patientList = cypressHelper.getPatients()

        def results = this.executor.execute(patientList, xmlString, MeasurementPeriod.getCalendarYear(new DateTime(2012,1,1,1,1).toDate()))

        def measureId = new XmlParser().parseText(xmlString).subjectOf.measureAttribute.value.find { it.@root == "2.16.840.1.113883.3.560.1" }.@extension

        cypressHelper.checkResults(measureId, results,
                {population, expected, actual, message ->
                    println message
                    assertEquals expected, actual, 0
                })
    }

}

