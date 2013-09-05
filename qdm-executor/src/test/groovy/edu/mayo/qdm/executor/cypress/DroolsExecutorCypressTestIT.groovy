package edu.mayo.qdm.executor.cypress
import edu.mayo.qdm.cypress.CypressPatientDataSource
import edu.mayo.qdm.cypress.CypressValidator
import edu.mayo.qdm.executor.MeasurementPeriod
import edu.mayo.qdm.executor.drools.DroolsExecutor
import edu.mayo.qdm.executor.drools.parser.Qdm2Drools
import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-executor-context.xml")
public class DroolsExecutorCypressTestIT {

	@Autowired
	private DroolsExecutor executor

    @Autowired
    private Qdm2Drools qdm2Drools

    def cypressDataSource = new CypressPatientDataSource()
    def cypressValidator = new CypressValidator()

    def slurper = new JsonSlurper()
	
	@Test
	public void TestSetUp(){
		assertNotNull(this.executor)
	}

    /*
     * 127 is fairly simple
     */
    @Test
    public void TestExecute127() throws IOException{
        doExecute("0043")
    }

    /*
     * 165 -- NQF 0018, High Blood Pressure
     */
    @Test
    public void TestExecute165() throws IOException{
        doExecute("0018")
    }

    /*
     * 124 -- NQF 0032, High Blood Pressure
     */
    @Test
    public void TestExecute124() throws IOException{
        doExecute("0032")
    }

    /*
     * 146 -- Some negation issues to work out...
     */
    @Test
    public void TestExecute0002() throws IOException{
        doExecute("0002")
    }

    /*
     * 137 -- Specific occurrences on the DENOM exceptions
     */
    @Test
    public void TestExecute0004() throws IOException{
        doExecute("0004")
    }

    /*
     * Negated specific occurrences on DENEX
     */
    @Test
    public void TestExecute0024() throws IOException{
        doExecute("0024")
    }

    @Test
    public void TestExecute0028() throws IOException{
        doExecute("0028")
    }

    @Test
    public void TestExecute0031() throws IOException{
        doExecute("0031")
    }

    @Test
    public void TestExecute0041() throws IOException{
        doExecute("0041")
    }

    @Test
    public void TestExecute0034() throws IOException{
        doExecute("0034")
    }

    @Test
    public void TestExecute0056() throws IOException{
        doExecute("0056")
    }

    @Test
    public void TestExecute0068() throws IOException{
        doExecute("0068")
    }

    @Test
    public void TestExecute0069() throws IOException{
        doExecute("0069")
    }

    @Test
    public void TestExecute0070() throws IOException{
        doExecute("0070")
    }

    @Test
    public void TestExecute0052() throws IOException{
        doExecute("0052")
    }

    /**
     * antinumerator question pending
     */
    @Test
    public void TestExecute0059() throws IOException{
        doExecute("0059")
    }

    @Test
    public void TestExecute0060() throws IOException{
        doExecute("0060")
    }

    @Test
    public void TestExecute0081() throws IOException{
        doExecute("0081")
    }

    @Test
    public void TestExecute0088() throws IOException{
        doExecute("0088")
    }

    /*
     * "FIRST" Group Operator
     *
     * Specific Occurrences on DENEX I think
     */
    @Test
    public void TestExecute0105() throws IOException{
        doExecute("0105")
    }

    @Test
    public void TestExecute0179() throws IOException{
        doExecute("0179")
    }

    @Test
    public void TestExecute0384() throws IOException{
        doExecute("0384")
    }

    @Test
    public void TestExecute0387() throws IOException{
        doExecute("0387")
    }

    @Test
    public void TestExecute0389() throws IOException{
        doExecute("0389")
    }

    @Test
    public void TestExecute0418() throws IOException{
        doExecute("0418")
    }

    @Test
    public void TestExecute0419() throws IOException{
        doExecute("0419")
    }

    @Test
    public void TestExecute0038() throws IOException{
        doExecute("0038")
    }

    @Test
    public void TestExecute1365() throws IOException{
        doExecute("1365")
    }

    @Test
    public void TestExecuteHIVRNAControl() throws IOException{
        doExecute("HIVRNAControl")
    }

    @Test
    public void TestDementiaCognitive() throws IOException{
        doExecute("DementiaCognitive")
    }

    @Test
    public void TestPrimaryCariesPrevention() throws IOException{
        doExecute("PrimaryCariesPrevention")
    }

    @Test
    public void TestCholesterolScreeningRisk() throws IOException{
        doExecute("CholesterolScreeningRisk")
    }

    @Test
    public void TestClosingReferralLoop() throws IOException{
        doExecute("ClosingReferralLoop")
    }

    void doExecute(measureId, strictCheck=true) throws IOException{
        /*
        qdm2Drools.metaClass.getJsonFromQdmFile = {
            xml ->
                slurper.parseText(
                        IOUtils.toString(new ClassPathResource("cypress/measures/ep/$measureId/hqmf_model.json").inputStream))
        }
        */

        def xmlStream = new ClassPathResource("cypress/measures/ep/${measureId}/hqmf1.xml").getInputStream()

        def xmlString = IOUtils.toString(xmlStream, "UTF-8")

        def patientList = cypressDataSource.getPatients()

        def results = this.executor.execute(patientList, xmlString, MeasurementPeriod.getCalendarYear(new DateTime(2012,1,1,1,1).toDate()))

        def measureIdUuid = new XmlParser().parseText(xmlString).id[0].@root

        def error
        cypressValidator.checkResults(measureIdUuid, results,
                {population, expected, actual, message ->
                    println message
                    try {
                        assertEquals expected, actual, 0
                    } catch (AssertionError e){
                        error = e
                    }
                })

        if(error != null) throw error
    }

}
