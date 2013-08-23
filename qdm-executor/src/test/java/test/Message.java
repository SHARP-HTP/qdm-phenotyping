package test;

import edu.mayo.qdm.patient.Patient;

/**
 */
public class Message {

    private String message;
    private Patient patent;


    public Message(String message, Patient patent) {
        this.message = message;
        this.patent = patent;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Patient getPatent() {
        return patent;
    }

    public void setPatent(Patient patent) {
        this.patent = patent;
    }
}
