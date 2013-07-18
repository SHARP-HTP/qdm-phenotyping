package edu.mayo.qdm.demographics

import edu.mayo.qdm.patient.Patient
import org.junit.Test

import static junit.framework.TestCase.assertEquals

/**
 */
class AgeDemographicsItemTest {

    @Test
    void testClassifyAge0(){
        def ageItem = new AgeDemographicsItem("IPP",new Patient(birthdate: new Date()))
        assertEquals "(0,18)", ageItem.label
    }

    @Test
    void testClassifyAgeOver80(){
        def ageItem = new AgeDemographicsItem("IPP",new Patient(birthdate: new Date(-2179162800000)))
        assertEquals "(75,above)", ageItem.label
    }

}
