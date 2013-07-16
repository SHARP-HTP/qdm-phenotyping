package edu.mayo.qdm.cypress

import edu.mayo.qdm.MeasurementPeriod
import edu.mayo.qdm.drools.DroolsExecutor
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.junit.Assert.*

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-executor-context.xml")
public class DroolsExecutorCypressTestIT {

	@Autowired
	private DroolsExecutor executor

    def cypressHelper = new CypressPatientHelper()
	
	@Test
	public void TestSetUp(){
		assertNotNull(this.executor)
	}

    /*
     * 127 is fairly simple
     */
    @Test
    public void TestExecute127() throws IOException{
        doExecute("CMS127v1")
    }

    /*
     * 165 -- NQF 18, High Blood Pressure
     */
    @Test
    public void TestExecute165() throws IOException{
        doExecute("CMS165v1")
    }

    @Test
    public void TestExecute124() throws IOException{
        doExecute("CMS124v1")
    }

    void doExecute(xmlFile) throws IOException{
        def xmlStream = new ClassPathResource("qdmxml/${xmlFile}.xml").getInputStream()

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
