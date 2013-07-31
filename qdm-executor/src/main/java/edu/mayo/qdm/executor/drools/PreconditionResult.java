package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

public class PreconditionResult {

	private String id;
	private Patient patient;
    private Event event;
    private Event specificOccurrence;

    public PreconditionResult(String id, Patient patient) {
        this(id, patient, null);
    }

    public PreconditionResult(String id, Patient patient, Event event) {
        super();
        this.id = id;
        this.patient = patient;
        this.event = event;
    }

    public PreconditionResult(String id, Patient patient, Event event, Event specificOccurrence) {
        super();
        this.id = id;
        this.patient = patient;
        this.event = event;
        this.specificOccurrence = specificOccurrence;
    }

    public Event getSpecificOccurrence() {
        return specificOccurrence;
    }

    public void setSpecificOccurrence(Event specificOccurrence) {
        this.specificOccurrence = specificOccurrence;
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

    public Event getEvent() {
        return event;
    }

}