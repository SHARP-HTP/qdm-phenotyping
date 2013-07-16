package edu.mayo.qdm.patient;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertNull;

public class PatientBirthdayTest {

    DateMidnight birthdate = new DateMidnight(1980, 1, 1);
    DateTime now = new DateTime();
    Years age = Years.yearsBetween(birthdate, now);

	@Test
	public void TestCalculateAge() throws java.lang.Exception {
		
		Date birthDay = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1980");
		

	}
	
	@Test(expected=IllegalStateException.class)
	public void TestSetBirthdateBad() throws java.lang.Exception {
		
		Date birthDay = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1980");
		
		Patient p = new Patient("test");

		
		p.setBirthdate(birthDay);
	}
	
	@Test
	public void TestSetBirthdateGood() throws java.lang.Exception {
		
		Date birthDay = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1980");
		
		Patient p = new Patient("test");
		

		p.setBirthdate(birthDay);
	}
	
	@Test
	public void TestSetBirthdateGoodWithNullAge() throws java.lang.Exception {
		
		Date birthDay = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1980");
		
		Patient p = new Patient("test");
		
		p.setBirthdate(birthDay);
	}
	

	@Test(expected=IllegalStateException.class)
	public void TestSetAgeBad() throws java.lang.Exception {
		
		Date birthDay = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1980");
		
		Patient p = new Patient("test");

		p.setBirthdate(birthDay);

	}
	
	@Test
	public void TestSetAgeGood() throws java.lang.Exception {
		
		Date birthDay = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1980");
		
		Patient p = new Patient("test");

		p.setBirthdate(birthDay);

	}
	
	@Test
	public void TestSetAgeGoodWithNullBirthdate() throws java.lang.Exception {
		Patient p = new Patient("test");

	}
	
	@Test
	public void TestSetAgeBirthdateStillNull() throws java.lang.Exception {
		Patient p = new Patient("test");

		assertNull(p.getBirthdate());
	}
	
	@Test
	public void TestSetBirthdateAgeCalculated() throws java.lang.Exception {
		Date birthDay = new SimpleDateFormat("MM/dd/yyyy").parse("01/01/1980");
		
		Patient p = new Patient("test");

		p.setBirthdate(birthDay);

	}

}
