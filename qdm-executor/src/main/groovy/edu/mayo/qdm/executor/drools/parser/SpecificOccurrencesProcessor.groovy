package edu.mayo.qdm.executor.drools.parser

import edu.mayo.qdm.executor.drools.parser.occurrences.TreeManager
import org.apache.commons.lang.BooleanUtils

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

        def occurrences = findSpecificOccurrences(json)

        sb.append(printMainOccurrencesRule(occurrences))

        occurrences.each { so ->

            json.population_criteria.each{ it.value.preconditions?.each {
                def leaves = []
                treeManager.getLeaves(json, it, so.id, so.constant, leaves)
                def rules = []
                treeManager.traverseUp(leaves, { rules << it })

                def root = rules.size > 0 ? rules.last() : null

                rules.unique().each { node ->
                    def fn = leaves.contains(node) ? referenceRule : conjunctionRule
                    sb.append(fn(node, so, node == root ))
                }
            } }
        }

        sb.toString()
    }

    def printMainOccurrencesRule(occurrences){
        """

        /* Rule */
        rule "Main Specific Occurrences Rule"
        dialect "mvel"
        no-loop

        when
            \$p : Patient( )
            ${occurrences.collect {
                """
                SpecificOccurrence(id == "${it.id}", constant == "${it.constant}", patient == \$p)
                """
            }.join()}

        then
            //System.out.println("MAIN OCCURRENCES!!!");
            insertLogical( new PreconditionResult("MAIN", \$p))
        end
        """
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

            //${node.negated ? "not(" : ""}
            PreconditionResult( id == "${node.id}", patient == \$p, \$event : event)
            //${node.negated ? ")" : ""}

        then

            ${
            if(isRoot){
                """
                System.out.println("Inserting SO: ${so.constant}, ${so.id}");
                insertLogical( new SpecificOccurrence("${so.constant}", "${so.id}", ${ (node.negated) ? "null" : "\$event"}, \$p))"""
            } else {
                """
                System.out.println("Inserting SO: ${node.id}");
                insertLogical( new SpecificOccurrenceResult("${node.id}", \$p, ${ (node.negated) ? "null" : "\$event"}))"""
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
                switch(node.conjunction){
                    case "allTrue" :
                        def conditions = []
                        node.children.findAll(hasSpecificOccurrence).eachWithIndex { child, idx ->
                            def eventString = (idx == 0) ? "\$event : event" : "event == \$event"

                            conditions <<
                        """
                        //${child.negated ? "not(" : ""}
                        SpecificOccurrenceResult(id == "${child.id}", patient == \$p, $eventString)
                        //${child.negated ? ")" : ""}"""  }

                        return conditions.join()
                    case "atLeastOneTrue" :
                        def moreThanOneChild = node.children.size > 1

                        def preamble =
                            (moreThanOneChild) ? """SpecificOccurrenceResult(${node.children.collect { """id == "${it.id}" """ }.join(" || ")}, patient == \$p, \$event : event)""" : ""
                        return """
                        $preamble
                        ${node.children.findAll(hasSpecificOccurrence).collect { child ->
                        """
                        ${
                            if(child.negated){
                                """/*not*/ ( SpecificOccurrenceResult(id == "${child.id}", patient == \$p, null) )"""
                            } else {
                                """SpecificOccurrenceResult(id == "${child.id}", patient == \$p, ${(moreThanOneChild)  ? "event == \$event" : "\$event : event"})"""
                            }
                        }
                        """}.join(" or ")}
                        """
                    default : throw new UnsupportedOperationException()

                }

            }

        then

            ${
            def allNegated = true
            node.children.findAll(hasSpecificOccurrence).each {
                allNegated &= (BooleanUtils.toBoolean(it.negated))
            }

            if(isRoot){
                """
                System.out.println("Inserting SO: ${so.constant}, ${so.id}");
                insertLogical(new SpecificOccurrence("${so.constant}", "${so.id}", ${allNegated ? "null" : "\$event"}, \$p)) """
            } else {
                """
                System.out.println("Inserting SO: ${node.id}");
                insertLogical(new SpecificOccurrenceResult("${node.id}", \$p, \$event)) """
            }
            }

        end

        """
    }

    def hasSpecificOccurrence = { node ->
        def isLeaf = node.isLeaf
        node.children?.each {
            isLeaf |= hasSpecificOccurrence(it)
        }

        isLeaf
    }

}
