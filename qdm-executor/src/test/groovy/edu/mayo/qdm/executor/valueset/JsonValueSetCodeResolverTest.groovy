package edu.mayo.qdm.executor.valueset
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 */
class JsonValueSetCodeResolverTest {

    @Test
    void TestGetCodes(){
        def resolver = new JsonValueSetCodeResolver()
        resolver.afterPropertiesSet()

        def codes = resolver.resolveConcpets("2.16.840.1.113883.3.117.1.7.1.212")

        assertTrue codes.size() > 0
    }
}


