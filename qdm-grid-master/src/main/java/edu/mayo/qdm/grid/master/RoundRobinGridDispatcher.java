package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.grid.common.WorkerExecutionRequest;
import edu.mayo.qdm.patient.Patient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

@Component
public class RoundRobinGridDispatcher implements GridDispatcher {

    @Autowired
    private Registrar registrar;

    private Logger log = Logger.getLogger(this.getClass());

    public Results dispatch(
            Iterable<Patient> patients,
            String qdmXml,
            MeasurementPeriod measurementPeriod,
            Map<String, String> valueSetDefinitions){
        while(this.registrar.getRegistrations().size() == 0){
            log.warn("Waiting for worker node...");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        Results results = new Results();

        for(Patient patient : patients){
            for(WorkerReference registration : this.registrar.getRegistrations()){
                Results returnedResults =
                        registration.getResults(new WorkerExecutionRequest(Arrays.asList(patient), qdmXml, measurementPeriod, valueSetDefinitions));
                for(Map.Entry<String, Set<Patient>> result : returnedResults.asMap().entrySet()){
                    results.addAll(result.getKey(), result.getValue());
                }
            }
        }

        return results;
    }
}
