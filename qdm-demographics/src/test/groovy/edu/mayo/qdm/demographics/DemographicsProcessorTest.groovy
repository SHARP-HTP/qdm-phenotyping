package edu.mayo.qdm.demographics

import edu.mayo.qdm.patient.Patient
import org.junit.Test

import static junit.framework.TestCase.assertEquals

/**
 */
class DemographicsProcessorTest {

    @Test
    void testGetDemographics() {
        def p = new DemographicsProcessor()
        def demos = p.getDemographics("IPP":[new Patient(birthdate: new Date())])

        def stat = demos.demographicType[0].demographicCategory[0].each {
            assertEquals 1, it.demographicStat.size()
        }

    }
}
