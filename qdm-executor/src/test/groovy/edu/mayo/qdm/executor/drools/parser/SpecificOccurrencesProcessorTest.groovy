package edu.mayo.qdm.executor.drools.parser

import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.junit.Test
import org.springframework.core.io.ClassPathResource

/**
 */
class SpecificOccurrencesProcessorTest {

    def slurper = new JsonSlurper()


    @Test
    void test(){
        def json = slurper.parseText(IOUtils.toString(new ClassPathResource("/cypress/measures/ep/0004/hqmf_model.json").inputStream))

        def processor = new SpecificOccurrencesProcessor()

        processor.getSpecificOccurrencesRules(json)


    }


}
