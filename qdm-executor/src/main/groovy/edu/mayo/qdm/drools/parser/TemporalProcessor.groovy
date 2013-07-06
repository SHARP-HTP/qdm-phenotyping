package edu.mayo.qdm.drools.parser

import org.apache.commons.lang.BooleanUtils

/**
 */
class TemporalProcessor {

    def processTemporalReferences(temporalReferences, property){
        temporalReferences.collect(processTemporalReference.rcurry(property)).join(" and ")
    }

    def processTemporalReference = {temporalReference, property ->
        def type = temporalReference.type

        if(this.hasProperty(type)){
            this."$type"(temporalReference, property)
        } else {
            throw new RuntimeException("Temporal Reference Type `$type` not recognized\n. JSON ->  $temporalReference")
        }
    }

    def SBE = {temporalReference, property ->
        "/*TODO*/"
    }

    def SAS = {temporalReference, property ->
        "/*TODO*/"
    }

    def SBS = {temporalReference, property ->

        def reference = temporalReference.reference

        def range = ""
        if(temporalReference.range){
            def rangeType = temporalReference.range.type
            if(this.hasProperty(rangeType)){
                range = this."$rangeType"(temporalReference.range, property)
            } else {
                throw new RuntimeException("Temporal Range Type `$rangeType` not recognized\n. JSON ->  $temporalReference")
            }
        }

        range
    }

    def IVL_PQ = {interval, property ->
        def sb = new StringBuilder()

        def low = interval.low
        if(low){
            sb.append(" $property ${BooleanUtils.toBoolean(low.'inclusive?') ? '>=' : '>'} ${low.value}")
        }
        def high = interval.high
        if(high){
            sb.append(" $property ${BooleanUtils.toBoolean(high.'inclusive?') ? '<=' : '<'} ${high.value}")
        }

        sb.toString()
    }

}
