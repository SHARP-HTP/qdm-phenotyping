package edu.mayo.qdm.drools;

import edu.mayo.qdm.MeasurementPeriod;
import edu.mayo.qdm.Results;
import edu.mayo.qdm.patient.*;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-executor-context.xml")
public class DroolsExecutorTestIT {

	@Autowired
	private DroolsExecutor executor;
	
	@Test
	public void TestSetUp(){
		assertNotNull(this.executor);
	}

	@Test
    /*
     * 117 is particularly large and complex
     */
	public void TestExecute117() throws IOException{
		InputStream xmlStream = new ClassPathResource("qdmxml/CMS117v1.xml").getInputStream();

        List<Patient> patientList = new ArrayList<Patient>();

        for(int i=0;i<1;i++){
		    Patient p = new Patient(Integer.toString(i));

            patientList.add(p);
        }
		
		Results results = this.executor.execute(patientList, IOUtils.toString(xmlStream), MeasurementPeriod.getCalendarYear(new Date()));

		assertEquals(1,results.get("IPP").size());
	}

    /*
     * 127 is fairly simple
     */
    @Test
    public void TestExecute127IPP() throws IOException{
        InputStream xmlStream = new ClassPathResource("qdmxml/CMS127v1.xml").getInputStream();

        List<Patient> patientList = new ArrayList<Patient>();

        //good
        Patient p1 = new Patient("1");

        p1.addEncounter(new Encounter("1", new Concept("G0439", "HCPCS", null), new Date(), new Date()));

        //invalid encounter - out
        Patient p2 = new Patient("2");

        p2.addEncounter(new Encounter("1", new Concept("__INVALID__", "SNOMEDCT", null), new Date(), new Date()));

        //not old enough - out
        Patient p3 = new Patient("3");

        p3.addEncounter(new Encounter("1", new Concept("G0439", "HCPCS", null), new Date(), new Date()));

        patientList.addAll(Arrays.asList(p1,p2,p3));

        Results results = this.executor.execute(patientList, IOUtils.toString(xmlStream), MeasurementPeriod.getCalendarYear(new Date()));

        assertEquals(1,results.get("IPP").size());
    }

    @Test
    public void TestExecute127NUMER() throws IOException{
        InputStream xmlStream = new ClassPathResource("qdmxml/CMS127v1.xml").getInputStream();

        List<Patient> patientList = new ArrayList<Patient>();

        //good
        Patient p1 = new Patient("1");

        p1.addMedication(new Medication(new Concept("33", "CVX", null), new Date(), new Date()));

        //good
        Patient p2 = new Patient("2");

        p2.addProcedure(new Procedure(new Concept("394678003", "SNOMEDCT", null), new Date(), new Date()));

        //invalid med -- out
        Patient p3 = new Patient("3");

        p3.addMedication(new Medication(new Concept("__INVALID__", "CVX", null), new Date(), new Date()));

        //invalid procedure -- out
        Patient p4 = new Patient("4");

        p4.addProcedure(new Procedure(new Concept("__INVALID__", "SNOMEDCT", null), new Date(), new Date()));

        patientList.addAll(Arrays.asList(p1,p2,p3,p4));

        Results results = this.executor.execute(patientList, IOUtils.toString(xmlStream), MeasurementPeriod.getCalendarYear(new Date()));

        assertEquals(2,results.get("NUMER").size());
    }

    @Test
    public void TestExecute124() throws IOException{
        InputStream xmlStream = new ClassPathResource("qdmxml/CMS124v1.xml").getInputStream();

        List<Patient> patientList = new ArrayList<Patient>();

        //wrong code -- out
        Patient p1 = new Patient("1");
        p1.setSex(Gender.FEMALE);

        p1.addEncounter(new Encounter("1", new Concept("__INVALID__", "SNOMEDCT", null), new Date(), new Date()));

        //wrong gender -- out
        Patient p2 = new Patient("2");
        p2.setSex(Gender.MALE);

        p2.addEncounter(new Encounter("1", new Concept("439708006", "SNOMEDCT", null), new Date(), new Date()));

        //good
        Patient p3 = new Patient("3");
        p3.setSex(Gender.FEMALE);

        p3.addEncounter(new Encounter("1",new Concept("439708006","SNOMEDCT", null),new Date(), new Date()));

        //good
        Patient p4 = new Patient("3");
        p4.setSex(Gender.FEMALE);

        p4.addEncounter(new Encounter("1",new Concept("185465003","SNOMEDCT", null),new Date(), new Date()));

        patientList.addAll(Arrays.asList(p1,p2,p3,p4));

        Results results = this.executor.execute(patientList, IOUtils.toString(xmlStream), MeasurementPeriod.getCalendarYear(new Date()));

        assertEquals(2,results.get("IPP").size());
    }

}
