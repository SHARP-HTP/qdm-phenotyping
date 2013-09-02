package edu.mayo.qdm.executor.drools.parser.occurrences

import groovy.json.JsonSlurper
import org.apache.commons.io.IOUtils
import org.junit.Test
import org.springframework.core.io.ClassPathResource

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals

/**
 */
class TreeManagerTest {

    def slurper = new JsonSlurper()

    @Test
    def void testTraverseUp(){
        def manager = new TreeManager()

        def root = new Node(id:"root")
        def and = new Node(id:"and", parent: root)
        def n1 = new Node(id:"1", parent: and)
        def n2 = new Node(id:"2", parent: and)

        manager.traverseUp([n1,n2], {
            assertNotEquals "root", it.id
        })

    }

    @Test
    def void testTraverseUpAll(){
        def manager = new TreeManager()

        def root = new Node(id:"root")
        def and = new Node(id:"and", parent: root)
        def n1 = new Node(id:"1", parent: and)
        def n2 = new Node(id:"2", parent: and)
        def n3 = new Node(id:"3", parent: root)

        def last
        manager.traverseUp([n1,n2,n3], {
            last = it.id
        })

        assertEquals "root", last

    }

    @Test
    def void testGetLeaves(){
        def manager = new TreeManager()

        def json = slurper.parseText(IOUtils.toString(new ClassPathResource("/cypress/measures/ep/0004/hqmf_model.json").inputStream))


        json.population_criteria.each{ it.value.preconditions?.each {
            def leaves = []
            manager.getLeaves(json, it, "A", "A", leaves)
            println "============================================"
            manager.traverseUp(leaves, {
                println it
            })
            println "============================================"
        } }



    }
}
