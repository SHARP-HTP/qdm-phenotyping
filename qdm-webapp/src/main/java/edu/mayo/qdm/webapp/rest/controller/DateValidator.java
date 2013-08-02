package edu.mayo.qdm.webapp.rest.controller;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class DateValidator implements InitializingBean {
	
	public enum DateType {START,END}
	
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
	
	private static final String LOWER_START_DATE_BOUND = "1-Jan-1995";
	private static final String UPPER_START_DATE_BOUND = "31-Dec-2002";
	private static final String LOWER_END_DATE_BOUND = "31-Dec-2010";
	private static final String UPPER_END_DATE_BOUND = "31-June-2012";
	
	private Date lowerStartDateBound;
	private Date upperStartDateBound;
	private Date lowerEndDateBound;
	private Date upperEndDateBound;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.lowerStartDateBound = this.dateFormat.parse(LOWER_START_DATE_BOUND);
		this.upperStartDateBound = this.dateFormat.parse(UPPER_START_DATE_BOUND);
		this.lowerEndDateBound = this.dateFormat.parse(LOWER_END_DATE_BOUND);
		this.upperEndDateBound = this.dateFormat.parse(UPPER_END_DATE_BOUND);
	}
	
	public void validateDate(String date, DateType type){
		Date dateToCheck = this.parse(date);
		
		Date lowerBound;
		Date upperBound;
		
		switch ( type ) {
			case START : {
				lowerBound = this.lowerStartDateBound;
				upperBound = this.upperStartDateBound;
				break;
			}
			case END : {
				lowerBound = this.lowerEndDateBound;
				upperBound = this.upperEndDateBound;
				break;	
			}
			default : {
				throw new IllegalStateException();
			}
		}
		
		if(dateToCheck.after(upperBound)){
			this.throwDateBoundException(dateToCheck, lowerBound, upperBound);
		}
		if(dateToCheck.before(lowerBound)){
			this.throwDateBoundException(dateToCheck, lowerBound, upperBound);
		}
	}
	
	private void throwDateBoundException(Date dateToCheck, Date lowerBound, Date upperBound){
		throw new UserInputException("Date: " + dateToCheck + " is out of the valid Date bounds: " +
				lowerBound + " " + upperBound);
	}
	
	protected Date parse(String date){
		try {
			return this.dateFormat.parse(date);
		} catch (ParseException e) {
			throw new UserInputException(e.getMessage());
		}
	}

}
