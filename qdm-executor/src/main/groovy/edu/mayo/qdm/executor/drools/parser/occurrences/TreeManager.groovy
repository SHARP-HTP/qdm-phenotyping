package edu.mayo.qdm.executor.drools.parser.occurrences

import edu.mayo.qdm.executor.drools.parser.SpecificOccurrencesProcessor

/**
 */
class TreeManager {

    def traverseUp(nodes, callback){
        if(nodes){
            def uniques = nodes.unique()

            uniques.each(callback)

            if(uniques.size() > 1){
                def parents = uniques.collect { it.parent }.findAll()

                traverseUp(parents, callback)
            }
        }
    }

    def getLeaves(measureJson, json, id, constant, leaves){
        def root = new Node()
        if(json.reference){
            root.id = json.reference
            root.isLeaf = true
        } else {
            root.id = json.id
            root.conjunction = json.conjunction_code
        }

        root.negated = json.negation

        if(json.preconditions == null){
            def so = getSpecificOccurrence(json.reference, measureJson)
            if(so != null && so.constant == constant && so.id == id){
                leaves << root
            } else {
                return null
            }
        } else {
            json.preconditions.each {
                def leaf = getLeaves(measureJson, it, id, constant, leaves)
                if(leaf != null){
                    leaf.parent = root
                    root.children << leaf
                }
            }
        }

        root
    }

    def getSpecificOccurrence(reference, measureJson){
        def criteria = measureJson.data_criteria.get(reference)
        if(criteria.specific_occurrence_const && criteria.specific_occurrence_const){
            new SpecificOccurrencesProcessor.SpecificOccurrence(
                    id: criteria.specific_occurrence,
                    constant: criteria.specific_occurrence_const)
        }
    }

}
