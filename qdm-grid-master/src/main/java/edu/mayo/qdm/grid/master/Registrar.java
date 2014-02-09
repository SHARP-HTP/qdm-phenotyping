package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.grid.common.WorkerRegistrationRequest;
import org.apache.camel.Handler;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Component("registrar")
public class Registrar {

    private Set<WorkerReference> workerReferences = new HashSet<WorkerReference>();

    @Autowired
    private ProducerTemplate producerTemplate;

    @Handler
    public void register(final WorkerRegistrationRequest workerRegistrationRequest) {
        System.out.println("Registering: " + workerRegistrationRequest.getUri());
        this.workerReferences.add(new WorkerReference(
                workerRegistrationRequest.getUri(),
                this.producerTemplate));
    }

    public Collection<WorkerReference> getRegistrations(){
        return this.workerReferences;
    }
}
