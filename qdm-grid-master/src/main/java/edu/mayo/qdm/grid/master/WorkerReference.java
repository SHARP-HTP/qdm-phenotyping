package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.grid.common.WorkerExecutionRequest;
import org.apache.camel.ProducerTemplate;

/**
 */
public class WorkerReference {

    private String uri;
    private ProducerTemplate producerTemplate;

    public WorkerReference(String uri, ProducerTemplate producerTemplate) {
        this.uri = uri;
        this.producerTemplate = producerTemplate;
    }

    public Results getResults(WorkerExecutionRequest workerExecutionRequest) {
        return (Results) this.producerTemplate.requestBody(this.uri, workerExecutionRequest);
    }

    public String getUri() {
        return uri;
    }
}
