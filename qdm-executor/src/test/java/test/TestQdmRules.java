package test;

import edu.mayo.qdm.executor.drools.AbstractDroolsTestBase;
import edu.mayo.qdm.patient.Gender;
import edu.mayo.qdm.patient.Patient;

import java.util.Arrays;

public class TestQdmRules extends AbstractDroolsTestBase {

    @Override
    protected Iterable<Patient> getPatients() {
        Patient p1 = new Patient("1");
        p1.setSex(Gender.MALE);

        Patient p2 = new Patient("1");
        p2.setSex(Gender.FEMALE);

        return Arrays.asList(p1,p2);
    }

    @Override
    protected String getDroolsFile() {
        return "testrule.drl";
    }
}
