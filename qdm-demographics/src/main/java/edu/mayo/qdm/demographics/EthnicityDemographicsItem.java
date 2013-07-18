package edu.mayo.qdm.demographics;

import edu.mayo.qdm.patient.Ethnicity;
import edu.mayo.qdm.patient.Patient;

/**
 */
public class EthnicityDemographicsItem extends AbstractDemographicsItem {

    private static final String ETHNICITY_STATISTIC = "ethnicity";

    protected EthnicityDemographicsItem(String population, Patient patient) {
        super(population, ETHNICITY_STATISTIC, patient);
    }

    @Override
    protected String getLabelFromPatient(Patient patient) {
        Ethnicity ethnicity = patient.getEthnicity();
        if(ethnicity == null){
            ethnicity = Ethnicity.UNKNOWN;
        }

        switch (ethnicity) {
            case HISPANICORLATINO:
                return "Hispanic or Latino";
            case NONHISPANICORLATINO:
                return "Non Hispanic or Latino";
            case UNKNOWN:
                return "Unknown";
            default:
                throw new IllegalStateException();
        }
    }
}
