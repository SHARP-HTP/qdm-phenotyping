package edu.mayo.qdm.demographics;

import edu.mayo.qdm.patient.Patient;
import edu.mayo.qdm.patient.Race;

/**
 */
public class RaceDemographicsItem extends AbstractDemographicsItem {

    private static final String RACE_STATISTIC = "race";

    protected RaceDemographicsItem(String population, Patient patient) {
        super(population, RACE_STATISTIC, patient);
    }

    @Override
    protected String getLabelFromPatient(Patient patient) {
        Race race = patient.getRace();
        if(race == null){
            race = Race.UNKNOWN;
        }

        switch (race) {
            case UNKNOWN:
                return "Unknown";
            case AMERICANINDIAN:
                return "Native Hawaiian";
            case ASIANINDIAN:
                return "Asian Indian";
            case ASIAN:
                return "Asian";
            case BLACKORAFRICANAMERICAN:
                return "Black or African American";
            case NATIVEHAWAIIAN:
                return "Native Hawaiian";
            case WHITE:
                return "White";
            case OTHER:
                return "Other";
            default:
                throw new IllegalStateException();
        }
    }
}
