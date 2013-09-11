package edu.mayo.qdm.executor.valueset

import edu.mayo.qdm.patient.Concept
import org.junit.Test

import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertTrue

class Cts2ValueSetCodeResolverTest {
    @Test
    void TestGetCodes(){
        def resolver = new Cts2ValueSetCodeResolver()
        resolver.afterPropertiesSet()

        def codes = resolver.resolveConcpets("2.16.840.1.113883.3.117.1.7.1.212")

        assertTrue codes.size() > 0
        assertTrue  resolver.isCodeInSet("2.16.840.1.113883.3.117.1.7.1.212", new Concept("430", "ICD9CM", "ICD9CM-2012"))
        assertFalse resolver.isCodeInSet("2.16.840.1.113883.3.117.1.7.1.212", new Concept("430", "ICD9CM", "ICD9CM-2013"))
        assertFalse resolver.isCodeInSet("2.16.840.1.113883.3.117.1.7.1.212", new Concept("432", "ICD9CM", "ICD9CM-2012"))
        assertTrue  resolver.isCodeInSet("2.16.840.1.113883.3.117.1.7.1.212", new Concept("430", "ICD9CM", null))
        assertTrue  resolver.isCodeInSet("2.16.840.1.113883.3.117.1.7.1.212", new Concept("430", "ICD-9-CM", null))
    }
}
