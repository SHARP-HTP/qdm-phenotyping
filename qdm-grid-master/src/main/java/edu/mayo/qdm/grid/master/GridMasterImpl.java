package edu.mayo.qdm.grid.master;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.QdmProcessor;
import edu.mayo.qdm.executor.ResultCallback;
import edu.mayo.qdm.executor.Results;
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

    public static void main(String[] args){
        AbstractApplicationContext context = new ClassPathXmlApplicationContext("qdm-grid-master-context.xml");
        context.registerShutdownHook();
    }

    @Override
    public void execute(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod, Map<String, String> valueSetDefinitions, ResultCallback callback) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Results execute(Iterable<Patient> patients, String qdmXml, MeasurementPeriod measurementPeriod, Map<String, String> valueSetDefinitions) {
        return this.gridDispatcher.dispatch(patients, qdmXml, measurementPeriod, valueSetDefinitions);
    }

    @Override
    public QdmProcessor getQdmProcessor(String qdmXml, MeasurementPeriod measurementPeriod, Map<String, String> valueSetDefinitions) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public GridStatus getStatus() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
