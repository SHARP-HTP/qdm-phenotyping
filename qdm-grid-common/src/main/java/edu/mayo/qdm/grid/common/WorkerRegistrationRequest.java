package edu.mayo.qdm.grid.common;

import java.io.Serializable;

/**
 */
public class WorkerRegistrationRequest implements Serializable {

    private String uri;

    public WorkerRegistrationRequest(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
