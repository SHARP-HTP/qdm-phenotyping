package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.cypress.CypressPatientDataSource;
import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.grid.worker.GridWorker;
import edu.mayo.qdm.patient.Patient;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-grid-master-context.xml")
public class TestGridIT {

    @Autowired
    private GridMaster gridMaster;

    @Test
    public void test() throws Exception {
        GridWorker.main(null);

        CypressPatientDataSource cypressDataSource = new CypressPatientDataSource();
        Iterable<Patient> patients = (Iterable<Patient>) cypressDataSource.getPatients();
        String qdmXml = IOUtils.toString(new ClassPathResource("cypress/measures/ep/0002/hqmf1.xml").getInputStream());
        System.out.println(
                this.gridMaster.execute(patients, qdmXml, MeasurementPeriod.getCalendarYear(new Date()), null));


    }
}
