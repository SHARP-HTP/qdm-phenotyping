package edu.mayo.qdm.drools;

import edu.mayo.qdm.patient.Patient;

public class PreconditionResult {

	private String id;
	private Patient patient;

	public PreconditionResult(String id, Patient patient) {
		super();
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
