package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

/**
 */
public class SpecificOccurrenceResult {

    private Event event;
    private String id;
    private Patient patient;

    public SpecificOccurrenceResult(String id, Patient patient, Event event) {
        this.patient = patient;
        this.event = event;
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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
