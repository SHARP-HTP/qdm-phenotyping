package edu.mayo.qdm.webapp.translator;

import edu.mayo.qdm.cypress.CypressPatientDataSource;
import edu.mayo.qdm.demographics.DemographicsProcessor;
import edu.mayo.qdm.demographics.model.Demographics;
import edu.mayo.qdm.executor.ExecutorFactory;
import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.patient.Patient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class Launcher implements InitializingBean {

    private DemographicsProcessor demographicsProcessor = new DemographicsProcessor();

    private ExecutorFactory factory;

    private List<Patient> patients;

    @Override
    public void afterPropertiesSet() throws Exception {
         this.factory = ExecutorFactory.instance();

        CypressPatientDataSource cypressPatientDataSource = new CypressPatientDataSource();

        this.patients = (List<Patient>) cypressPatientDataSource.getPatients();
    }

    public ExecutionResult launchTranslator(
			String qdmXml,
			Date startDate,
			Date endDate) throws Exception {
        Results results =
            factory.getExecutor().execute(patients, qdmXml, new MeasurementPeriod(startDate, true, endDate, true));

        Demographics demographics = this.demographicsProcessor.getDemographics(results.asMap());

        ExecutionResult result = new ExecutionResult(this.demographicsProcessor.toXml(demographics));

        return result;
    }

}
