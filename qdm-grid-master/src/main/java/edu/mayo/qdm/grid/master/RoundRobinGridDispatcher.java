package edu.mayo.qdm.grid.master;

import com.google.common.collect.Iterables;
import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.grid.common.WorkerExecutionRequest;
import edu.mayo.qdm.patient.Patient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Component
public class RoundRobinGridDispatcher implements GridDispatcher {

    private static int PARTITION_SIZE = 100;

    @Autowired
    private Registrar registrar;

    private Logger log = Logger.getLogger(this.getClass());

    private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

    public Results dispatch(
            final Iterable<Patient> patients,
            final String qdmXml,
            final MeasurementPeriod measurementPeriod,
            final Map<String, String> valueSetDefinitions){
        while(this.registrar.getRegistrations().size() == 0){
            log.warn("Waiting for worker node...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        final Results results = new Results();

        for(final List<Patient> partition : this.partition(patients, PARTITION_SIZE)){
            Collection<WorkerReference> workersCollection = this.registrar.getRegistrations();

            this.executorService.setCorePoolSize(workersCollection.size());

            Iterator<WorkerReference> workers = workersCollection.iterator();

            while(workers.hasNext()){
                final WorkerReference registration = workers.next();

                final Runnable job = new Runnable(){

                    @Override
                    public void run() {
                        Results results = registration.getResults(new WorkerExecutionRequest(new ArrayList(partition), qdmXml, measurementPeriod, valueSetDefinitions));
                        System.out.println(results.asMap());
                        for(Map.Entry<String, Set<Patient>> result : results.asMap().entrySet()){
                            results.addAll(result.getKey(), result.getValue());
                        }
                    }
                };

                this.executorService.submit(job);
            }
        }

        this.executorService.shutdown();

        return results;
    }

    protected Iterable<List<Patient>> partition(Iterable <Patient> patients, int partitions){
        return Iterables.partition(patients, partitions);
    }
}
