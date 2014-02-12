package edu.mayo.qdm.grid.master;

import com.google.common.collect.AbstractIterator;
import edu.mayo.qdm.cypress.CypressPatientDataSource;
import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.ResultCallback;
import edu.mayo.qdm.grid.worker.GridWorker;
import edu.mayo.qdm.patient.Patient;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-grid-master-context.xml")
public class TestGridIT {

    @Autowired
    private GridMaster gridMaster;

    @Test
    public void test() throws Exception {
        for(int i=0;i<20;i++){
            GridWorker.main(new String[]{"localhost", "515" + Integer.toString(i), "localhost", "1984"});
        }

        Iterable <Patient> patients = new Iterable<Patient>(){

            @Override
            public Iterator<Patient> iterator() {
                return multiply(100);
            }
        };

        String qdmXml = IOUtils.toString(new ClassPathResource("cypress/measures/ep/0043/hqmf1.xml").getInputStream());

        this.gridMaster.execute(patients, qdmXml, MeasurementPeriod.getCalendarYear(new DateTime(2012, 6, 1, 1, 1).toDate()), null, new ResultCallback() {
            @Override
            public void hit(String population, Patient patient) {
                //
            }
        });
    }

    public Iterator<Patient> multiply(final int size){
    return new AbstractIterator<Patient>() {

        CypressPatientDataSource cypressDataSource = new CypressPatientDataSource();

        int counter = 0;

        List<Patient> patients = (List<Patient>) cypressDataSource.getPatients();
        Iterator<Patient> cache = patients.iterator();

        @Override
        protected Patient computeNext() {
            if(counter > size){
                return this.endOfData();
            }
            if(!cache.hasNext()){
                Collections.shuffle(patients);
                cache = patients.iterator();
                counter++;
            }
            Patient p;
            try {
                p = (Patient) cache.next().clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            p.setSourcePid(UUID.randomUUID().toString());

            return p;
        }
    };
    }
}
