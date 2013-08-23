package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

public class PreconditionResult2 {

	private String id;
	private Patient patient;
    private Event event;
    private String specificOccurrence;

    public PreconditionResult2(String id, Patient patient, Event event, String specificOccurrence) {
        super();
        this.id = id;
        this.patient = patient;
        this.event = event;
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

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getSpecificOccurrence() {
        return specificOccurrence;
    }

    public void setSpecificOccurrence(String specificOccurrence) {
        this.specificOccurrence = specificOccurrence;
    }
}
