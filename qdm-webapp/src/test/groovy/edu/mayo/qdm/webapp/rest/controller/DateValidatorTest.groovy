package edu.mayo.qdm.webapp.rest.controller

import org.junit.Test

import edu.mayo.qdm.webapp.rest.controller.DateValidator.DateType


class DateValidatorTest {

	@Test
	void TestStartCheckDateGood() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("11-JAN-2000", DateType.START)
	}
	
	@Test
	void TestStartCheckDateGoodStartInclusive() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("1-Jan-1995", DateType.START)
	}
	
	@Test
	void TestStartCheckDateGoodEndInclusive() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("31-Dec-2002", DateType.START)
	}
	
	
	@Test(expected=UserInputException)
	void TestStartCheckDateBadStartInclusive() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("31-Dec-1994", DateType.START)
	}
	
	@Test(expected=UserInputException)
	void TestStartCheckDateBadEndInclusive() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("01-Jan-2003", DateType.START)
	}
	
	@Test(expected=UserInputException)
	void TestStartCheckDateUnder() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("11-JAN-1900", DateType.START)
	}
	
	@Test(expected=UserInputException)
	void TestStartCheckDateOver() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("11-JAN-2100", DateType.START)
	}
	
	@Test(expected=UserInputException)
	void TestStartCheckDateInvalid() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("23-345-3421", DateType.START)
	}
	
	
	
	@Test
	void TestEndCheckDateGood() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("11-JAN-2011", DateType.END)
	}
	
	@Test
	void TestEndCheckDateGoodStartInclusive() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("31-Dec-2010", DateType.END)
	}
	
	@Test
	void TestEndCheckDateGoodEndInclusive() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("31-June-2012", DateType.END)
	}
	
	@Test(expected=UserInputException)
	void TestEndCheckDateBadEndInclusive() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("02-Jul-2012", DateType.END)
	}
	
	@Test(expected=UserInputException)
	void TestEndCheckDateBadStartInclusive() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("30-Dec-2010", DateType.END)
	}
	
	
	@Test(expected=UserInputException)
	void TestEndCheckDateUnder() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("30-JAN-2010", DateType.END)
	}
	
	@Test(expected=UserInputException)
	void TestEndCheckDateOver() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("31-JAN-2100", DateType.END)
	}
	
	@Test(expected=UserInputException)
	void TestEndCheckDateInvalid() {
		def controller = new DateValidator();
		controller.afterPropertiesSet()
		
		controller.validateDate("23-345-3421", DateType.END)
	}
	
	
}
