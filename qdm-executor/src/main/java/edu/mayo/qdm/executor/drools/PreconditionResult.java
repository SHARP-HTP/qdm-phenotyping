package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.patient.Event;
import edu.mayo.qdm.patient.Patient;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PreconditionResult {

	private String id;
	private Patient patient;
    private Set<Event> events = new HashSet<Event>();
    private PreconditionResultStatus status = PreconditionResultStatus.NOT_EXECUTED;

    public PreconditionResult(String id, Patient patient) {
        super();
        this.id = id;
        this.patient = patient;
    }

    public PreconditionResult(String id, Patient patient, Set<Event> events) {
        super();
        this.id = id;
        this.patient = patient;
        this.events = events;
    }

    public PreconditionResult(String id, Patient patient, Event event) {
        super();
        this.id = id;
        this.patient = patient;
        this.events = new HashSet<Event>(Arrays.asList(event));
    }

    public void setExecuted(){
        if(this.status.equals(PreconditionResultStatus.NOT_EXECUTED)){
            this.status = PreconditionResultStatus.EXECUTED;
        }
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

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvent(Event event) {
        this.events = new HashSet<Event>(Arrays.asList(event));
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    public PreconditionResultStatus getStatus() {
        return status;
    }

    public void setStatus(PreconditionResultStatus status) {
        this.status = status;
    }
}
