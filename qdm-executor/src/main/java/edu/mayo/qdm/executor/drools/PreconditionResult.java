package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.Set;

public class PreconditionResult {

	private String id;
	private Patient patient;
    private Set<Event> temporalEvents;

    public PreconditionResult(String id, Patient patient) {
        this(id, patient, null);
    }

	public PreconditionResult(String id, Patient patient, Set<Event> temporalEvents) {
		super();
		this.id = id;
		this.patient = patient;
        this.temporalEvents = temporalEvents;
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
