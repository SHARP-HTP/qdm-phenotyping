package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.ResultCallback;
import edu.mayo.qdm.patient.Patient;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GridMasterImpl implements GridMaster {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private GridDispatcher gridDispatcher;

    @Autowired
    private Registrar registrar;

    public static void main(String[] args){
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("qdm-grid-master-context.xml");
        context.registerShutdownHook();
    }

    @Override
    public void execute(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod, Map<String, String> valueSetDefinitions, ResultCallback callback) {
        this.gridDispatcher.dispatch(patients, qdmXml, measurementPeriod, valueSetDefinitions, callback);
    }

    @Override
    public GridStatus getStatus() {
        return new GridStatus(this.registrar.getRegistrations().size());
    }
}
