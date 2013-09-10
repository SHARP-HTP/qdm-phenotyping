package edu.mayo.qdm.executor.valueset

import edu.mayo.qdm.patient.Concept
import groovy.json.JsonSlurper
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component

/**
 */
@Component
class JsonValueSetCodeResolver implements ValueSetCodeResolver, InitializingBean {

    def slurper = new JsonSlurper()

    def valueSetMap = [] as HashMap

    def codeMap = [] as HashMap

    @Override
    void afterPropertiesSet() throws Exception {
        def resolver = new PathMatchingResourcePatternResolver()
        resolver.getResources("classpath:/value_sets/json/*.json").
                collect { new InputStreamReader(it.getInputStream()) }.
                collect { slurper.parse(it) }.
                each {
                    def oid = it.oid

                    def concepts = it.concepts.collect {
                        new Concept(
                                code: it.code,
                                codingScheme: it.code_system_name,
                                codingSchemeVersion: it.code_system_version)
                    }

                    valueSetMap.put(oid, concepts)

                    concepts.each {
                        codeMap.get(it.code + oid, new HashSet()).add(it)
                    }
                }
    }

    @Override
    Set<Concept> resolveConcpets(String valueSetOid) {
        this.valueSetMap.get(valueSetOid)
    }

    @Override
    boolean isCodeInSet(String valueSetOid, Concept concept) {
        def candidates = this.codeMap.get(concept.code + valueSetOid)

        candidates?.find { it.matches(concept) } != null
    }
}
