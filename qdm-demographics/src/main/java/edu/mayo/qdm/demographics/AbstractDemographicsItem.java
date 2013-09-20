package edu.mayo.qdm.demographics;

import edu.mayo.qdm.patient.Patient;

/**
 */
public abstract class AbstractDemographicsItem {

    private String population;
    private String statistic;
    private String label;

    protected AbstractDemographicsItem(String population, String statistic, Patient patient) {
        super();
        this.population = population;
        this.statistic = statistic;
        this.label = this.getLabelFromPatient(patient);
    }

    protected abstract String getLabelFromPatient(Patient patient);

    protected String getPopulation() {
        return population;
    }

    protected String getStatistic() {
        return statistic;
    }

    protected String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof AbstractDemographicsItem){
            final AbstractDemographicsItem other = (AbstractDemographicsItem) obj;
            return population.equals(other.population)
              && statistic.equals(other.statistic)
              && label.equals(other.label);
        } else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 31 * (population != null ? population.hashCode() : 0);
    }
}
