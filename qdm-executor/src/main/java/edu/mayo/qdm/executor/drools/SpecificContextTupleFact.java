package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Patient;

import java.util.List;

/**
 */
public class SpecificContextTupleFact extends SpecificContextTuple {

    private String id;
    private Patient patient;

    public SpecificContextTupleFact(String id, Patient patient, List<SpecificOccurrence> occurrences) {
        super(null);
        this.id = id;
        this.patient = patient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
