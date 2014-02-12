package edu.mayo.qdm.grid.master;

import com.google.common.collect.AbstractIterator;
import edu.mayo.qdm.cypress.CypressPatientDataSource;
import edu.mayo.qdm.executor.MeasurementPeriod;
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

import java.util.Iterator;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-grid-master-context.xml")
public class TestGridIT {

    @Autowired
    private GridMaster gridMaster;

    @Test
    public void test() throws Exception {
        GridWorker.main(new String[]{"localhost", "5150", "localhost", "1984"});

        Iterable<Patient> patients = new Iterable<Patient>(){

            @Override
            public Iterator<Patient> iterator() {
                return multiply(100);
            }
        };

        String qdmXml = IOUtils.toString(new ClassPathResource("cypress/measures/ep/0043/hqmf1.xml").getInputStream());

        System.out.println(
                this.gridMaster.execute(patients, qdmXml, MeasurementPeriod.getCalendarYear(new DateTime(2012,6,1,1,1).toDate()), null).asMap());
    }

    public Iterator<Patient> multiply(final int size){
    return new AbstractIterator<Patient>() {

        CypressPatientDataSource cypressDataSource = new CypressPatientDataSource();

        int counter = 0;
        Iterator<Patient> cache = ((List<Patient>) cypressDataSource.getPatients()).iterator();

        @Override
        protected Patient computeNext() {
            if(counter > size){
                return this.endOfData();
            }
            if(!cache.hasNext()){
                cache = ((List<Patient>) cypressDataSource.getPatients()).iterator();
                counter++;
            }
            return cache.next();
        }
    };
    }
}
