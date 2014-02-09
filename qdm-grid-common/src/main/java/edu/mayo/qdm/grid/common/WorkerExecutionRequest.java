package edu.mayo.qdm.grid.common;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.patient.Patient;

import java.io.Serializable;
import java.util.Map;

/**
 */
public class WorkerExecutionRequest implements Serializable {

    private Iterable<Patient> patients;
    private String qdmXml;
    private MeasurementPeriod measurementPeriod;
    private Map<String, String> valueSetDefinitions;

    public WorkerExecutionRequest(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod, Map<String, String> valueSetDefinitions) {
        this.patients = patients;
        this.qdmXml = qdmXml;
        this.measurementPeriod = measurementPeriod;
        this.valueSetDefinitions = valueSetDefinitions;
    }

    public Iterable<Patient> getPatients() {
        return patients;
    }

    public String getQdmXml() {
        return qdmXml;
    }

    public MeasurementPeriod getMeasurementPeriod() {
        return measurementPeriod;
    }

    public Map<String, String> getValueSetDefinitions() {
        return valueSetDefinitions;
    }
}
