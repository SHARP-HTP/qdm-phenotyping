package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

public class PreconditionResult {

	private String id;
	private Patient patient;
    private Event event;
    private boolean negative = false;

    public PreconditionResult(String id, Patient patient) {
        super();
        this.id = id;
        this.patient = patient;
    }

    public PreconditionResult(String id, Patient patient, Event event) {
        super();
        this.id = id;
        this.patient = patient;
        this.event = event;
    }

    public PreconditionResult(String id, Patient patient, Event event, boolean negative) {
        super();
        this.id = id;
        this.patient = patient;
        this.event = event;
        this.negative = negative;
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

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }
}
