package edu.mayo.qdm.grid.master;

import com.google.common.collect.Iterables;
import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.ResultCallback;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.grid.common.WorkerExecutionRequest;
import edu.mayo.qdm.patient.Patient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class RoundRobinGridDispatcher implements GridDispatcher {

    private static int PARTITION_SIZE = 1000;

    @Autowired
    private Registrar registrar;

    private Logger log = Logger.getLogger(this.getClass());

    @Override
    public void dispatch(
            final Iterable<Patient> patients,
            final String qdmXml,
            final MeasurementPeriod measurementPeriod,
            final Map<String, String> valueSetDefinitions,
            final ResultCallback callback){
        while(this.registrar.getRegistrations().size() == 0){
            log.warn("Waiting for worker node...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);

        final Integer[] total = {0};

        Random random = new Random();

        for(final List<Patient> partition : this.partition(patients, PARTITION_SIZE)){
            List<WorkerReference> workersCollection = new ArrayList(this.registrar.getRegistrations());

            final WorkerReference registration = workersCollection.get(random.nextInt(workersCollection.size()));

                final Runnable job = new Runnable(){

                    @Override
                    public void run() {
                        try {
                            log.debug("Processing " +  (total[0] += partition.size()) + " results with Worker Node " + registration.getUri());
                            Results workerResults = registration.getResults(new WorkerExecutionRequest(new ArrayList(partition), qdmXml, measurementPeriod, valueSetDefinitions));
                            for(Map.Entry<String, Set<Patient>> result : workerResults.asMap().entrySet()){
                                for(Patient hit : result.getValue()){
                                    callback.hit(result.getKey(), hit);
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                };

                executorService.submit(job);
            }

        try {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected Iterable<List<Patient>> partition(Iterable <Patient> patients, int partitions){
        return Iterables.partition(patients, partitions);
    }

}
