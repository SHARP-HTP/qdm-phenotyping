package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.ResultCallback;
import edu.mayo.qdm.patient.Patient;

import java.util.Map;

/**
 */
public interface GridDispatcher {

    public void dispatch(
            Iterable<Patient> patients,
            String qdmXml,
            MeasurementPeriod measurementPeriod,
            Map<String, String> valueSetDefinitions,
            ResultCallback callback);

}


