package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.cypress.CypressPatientDataSource;
import edu.mayo.qdm.patient.Patient;

public class Test0052Rules extends AbstractDroolsTestBase {

    @Override
    protected Iterable<Patient> getPatients() {
        CypressPatientDataSource ds = new CypressPatientDataSource();

        return (Iterable<Patient>) ds.getPatients();
    }


    @Override
    protected String getDroolsFile() {
        return "0052.drl";
    }
}
