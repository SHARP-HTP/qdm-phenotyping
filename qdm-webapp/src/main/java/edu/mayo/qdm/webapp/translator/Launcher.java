package edu.mayo.qdm.webapp.translator;

import edu.mayo.qdm.cypress.CypressPatientDataSource;
import edu.mayo.qdm.demographics.DemographicsProcessor;
import edu.mayo.qdm.demographics.model.Demographics;
import edu.mayo.qdm.executor.ExecutorFactory;
import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.Results;
import edu.mayo.qdm.patient.Patient;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class Launcher {

    private DemographicsProcessor demographicsProcessor = new DemographicsProcessor();

	public ExecutionResult launchTranslator(
			String qdmXml,
			Date startDate,
			Date endDate) throws Exception {

        ExecutorFactory factory = ExecutorFactory.instance();

        CypressPatientDataSource cypressPatientDataSource = new CypressPatientDataSource();

        List<Patient> patients = (List<Patient>) cypressPatientDataSource.getPatients();

        Results results =
            factory.getExecutor().execute(patients, qdmXml, MeasurementPeriod.getCalendarYear(new DateTime(2012,1,1,1,1).toDate()));

        Demographics demographics = this.demographicsProcessor.getDemographics(results.asMap());

        ExecutionResult result = new ExecutionResult(this.demographicsProcessor.toXml(demographics));

        return result;
    }

}
