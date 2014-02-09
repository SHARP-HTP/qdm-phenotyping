package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.patient.Patient;

import java.util.Map;

/**
 */
public interface GridDispatcher {

    public Results dispatch(
            Iterable<Patient> patients,
            String qdmXml,
            MeasurementPeriod measurementPeriod,
            Map<String, String> valueSetDefinitions);

}


