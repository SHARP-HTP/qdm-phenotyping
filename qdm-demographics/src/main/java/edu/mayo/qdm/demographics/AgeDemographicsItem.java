package edu.mayo.qdm.demographics;

import edu.mayo.qdm.patient.Patient;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 */
public class AgeDemographicsItem extends AbstractDemographicsItem {

    private static final String AGE_STATISTIC = "age";

    private static List<Integer> CATEGORIES = Arrays.asList(0,18,30,60,75);

    protected AgeDemographicsItem(String population, Patient patient) {
        super(population, AGE_STATISTIC, patient);
    }

    @Override
    protected String getLabelFromPatient(Patient patient) {
        int age = patient.getAge(new Date());
        return this.classifyAge(age);
    }

    protected String classifyAge(int age){
        for(int i=0;i<CATEGORIES.size();i++){
            if(CATEGORIES.get(i) > age){
                return "(" + CATEGORIES.get(i-1) + "," + CATEGORIES.get(i) + ")";
            }
        }

        return "(" + CATEGORIES.get(CATEGORIES.size()-1) + ",above)";
    }

}
