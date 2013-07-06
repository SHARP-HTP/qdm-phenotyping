package edu.mayo.qdm.drools;

import edu.mayo.qdm.Results;
import edu.mayo.qdm.patient.Concept;
import edu.mayo.qdm.patient.Encounter;
import edu.mayo.qdm.patient.Gender;
import edu.mayo.qdm.patient.Patient;
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
	        p.setAge(1);
            patientList.add(p);
        }
		
		Results results = this.executor.execute(patientList, IOUtils.toString(xmlStream));

		assertEquals(1,results.get("IPP").size());
	}

    /*
     * 127 is fairly simple
     */
    @Test
         public void TestExecute127() throws IOException{
        InputStream xmlStream = new ClassPathResource("qdmxml/CMS127v1.xml").getInputStream();

        List<Patient> patientList = new ArrayList<Patient>();

        Patient p1 = new Patient("1");
        p1.setAge(65);

        Patient p2 = new Patient("2");
        p2.setAge(99);

        Patient p3 = new Patient("3");
        p3.setAge(64);

        patientList.addAll(Arrays.asList(p1,p2,p3));

        Results results = this.executor.execute(patientList, IOUtils.toString(xmlStream));

        assertEquals(2,results.get("IPP").size());
    }

    @Test
    public void TestExecute124() throws IOException{
        InputStream xmlStream = new ClassPathResource("qdmxml/CMS124v1.xml").getInputStream();

        List<Patient> patientList = new ArrayList<Patient>();

        //wrong code -- out
        Patient p1 = new Patient("1");
        p1.setSex(Gender.FEMALE);
        p1.setAge(30);
        p1.addEncounter(new Encounter("1", new Concept("__INVALID__", "SNOMEDCT", null), new Date()));

        //wrong gender -- out
        Patient p2 = new Patient("2");
        p2.setSex(Gender.MALE);
        p2.setAge(30);
        p2.addEncounter(new Encounter("1", new Concept("439708006", "SNOMEDCT", null), new Date()));

        //good
        Patient p3 = new Patient("3");
        p3.setSex(Gender.FEMALE);
        p3.setAge(30);
        p3.addEncounter(new Encounter("1",new Concept("439708006","SNOMEDCT", null),new Date()));

        //good
        Patient p4 = new Patient("3");
        p4.setSex(Gender.FEMALE);
        p4.setAge(31);
        p4.addEncounter(new Encounter("1",new Concept("185465003","SNOMEDCT", null),new Date()));

        patientList.addAll(Arrays.asList(p1,p2,p3,p4));

        Results results = this.executor.execute(patientList, IOUtils.toString(xmlStream));

        assertEquals(2,results.get("IPP").size());
    }

}
