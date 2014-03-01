package test;

import edu.mayo.qdm.executor.drools.AbstractDroolsTestBase;
import edu.mayo.qdm.patient.Encounter;
import edu.mayo.qdm.patient.Gender;
import edu.mayo.qdm.patient.Patient;
import org.joda.time.DateTime;

import java.util.Arrays;
import java.util.Date;

public class TestQdmRules extends AbstractDroolsTestBase {

    @Override
    protected Iterable<Patient> getPatients() {
        Patient p1 = new Patient("1");
        p1.setSex(Gender.MALE);
        p1.setBirthdate(new Date(0));
        p1.addEncounter(new Encounter(null, null, new DateTime(2013,2,1,1,1).toDate()));


        Patient p2 = new Patient("2");
        p2.setSex(Gender.FEMALE);
        p2.setBirthdate(new Date(0));

        return Arrays.asList(p1,p2);
    }

    @Override
    protected String getDroolsFile() {
        return "testrule.drl";
    }

    /*
    @Override
    protected Iterable<?> getOtherFacts() {
        return Arrays.asList(MeasurementPeriod.getCalendarYear(new DateTime(2014,1,1,1,1).toDate()));
    }
    */
}
