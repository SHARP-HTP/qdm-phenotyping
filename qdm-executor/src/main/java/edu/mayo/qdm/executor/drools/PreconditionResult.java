package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

public class PreconditionResult {

	private String id;
	private Patient patient;
    private Event event;
    //private Set<Event> events;

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

    /*
    public PreconditionResult(String id, Patient patient, Set<Event> events) {
        super();
        this.id = id;
        this.patient = patient;
        this.events = events;
    }
    */

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

    /*
    public Set<Event> getEvents() {
        return events;
    }
    */

}
