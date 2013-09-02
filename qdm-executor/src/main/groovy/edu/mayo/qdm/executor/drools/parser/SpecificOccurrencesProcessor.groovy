package edu.mayo.qdm.executor.drools.parser

import edu.mayo.qdm.executor.drools.parser.occurrences.TreeManager
/**
 */
class SpecificOccurrencesProcessor {

    def treeManager = new TreeManager()

    static class SpecificOccurrence{
        def id
        def constant
    }

    def getSpecificOccurrencesRules(json){

        def sb = new StringBuffer()

        findSpecificOccurrences(json).each { so ->

            json.population_criteria.each{ it.value.preconditions?.each {
                def leaves = []
                treeManager.getLeaves(json, it, so.id, so.constant, leaves)
                def rules = []
                treeManager.traverseUp(leaves, { rules << it })

                rules.eachWithIndex { node, idx ->
                    def fn = leaves.contains(node) ? referenceRule : conjunctionRule
                    sb.append(fn(node, so, idx == (rules.size() - 1) ))
                }
            } }
        }

        sb.toString()
    }

    private def findSpecificOccurrences(json){
        def specificOccurrences = json.source_data_criteria.collect {
            if(it.value.specific_occurrence_const){
                def constant = it.value.specific_occurrence_const
                def id = it.value.specific_occurrence

                new SpecificOccurrence(id: id, constant: constant)
            }
        }.findAll()

        specificOccurrences
    }

    private def referenceRule = { node, so, isRoot ->
        """

        /* Rule */
        rule "Specific Occurrence (${so.constant}, ${so.id}) Rule ID: ${node.id}"
        dialect "mvel"
        no-loop

        when
            \$p : Patient( )

            PreconditionResult( id == "${node.id}", patient == \$p, \$event : event)

        then
            ${
            if(isRoot){
                """
                insert( new SpecificOccurrence("${so.constant}", "${so.id}", \$event, \$p))"""
            } else {
                """
                insert( new SpecificOccurrenceResult("${node.id}", \$p, \$event))"""
            }
            }

        end

        """
    }

    private def conjunctionRule = { node, so, isRoot ->
        """

        /* Rule */
        rule "Specific Occurrence (${so.constant}, ${so.id}) Rule ID: ${node.id}"
        dialect "mvel"
        no-loop

        when
            \$p : Patient( )

            ${
                def conditions = []
                node.children.eachWithIndex { child, idx ->
                    def eventString = (idx == 0) ? "\$event : event" : "event == \$event"

                    conditions <<
                """
                SpecificOccurrenceResult(id == "${child.id}", patient == \$p, $eventString)"""  }

                switch(node.conjunction){
                    case "allTrue" :
                        return conditions.join()
                    case "atLeastOneTrue" :
                        return conditions.join("\nor")
                    default : throw new UnsupportedOperationException()

                }

            }

        then
            ${
            if(isRoot){
                """
                insert(new SpecificOccurrence("${so.constant}", "${so.id}", \$event, \$p)) """
            } else {
                """
                insert(new SpecificOccurrenceResult("${node.id}", \$p, \$event)) """
            }
            }

        end

        """
    }


}
