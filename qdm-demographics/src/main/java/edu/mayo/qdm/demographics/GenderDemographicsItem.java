package edu.mayo.qdm.demographics;

import edu.mayo.qdm.patient.Gender;
import edu.mayo.qdm.patient.Patient;

/**
 */
public class GenderDemographicsItem extends AbstractDemographicsItem {

    private static final String GENDER_STATISTIC = "gender";

    protected GenderDemographicsItem(String population, Patient patient) {
        super(population, GENDER_STATISTIC, patient);
    }

    @Override
    protected String getLabelFromPatient(Patient patient) {
        Gender gender = patient.getSex();
        if(gender == null){
            gender = Gender.UNKNOWN;
        }

        switch (gender) {
            case MALE:
                return "male";
            case FEMALE:
                return "female";
            case UNKNOWN:
                return "unknown";
            default:
                throw new IllegalStateException();
        }
    }
}
