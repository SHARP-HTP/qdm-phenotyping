package edu.mayo.qdm.demographics;

import edu.mayo.qdm.patient.Patient;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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
			return new EqualsBuilder()
			  .append(population, other.population)
			  .append(statistic, other.statistic)
			  .append(label, other.label)
			  .isEquals();
		} else{
			return false;
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
		  .append(population)
		  .append(statistic)
		  .append(label)
		  .toHashCode();
	}
}
