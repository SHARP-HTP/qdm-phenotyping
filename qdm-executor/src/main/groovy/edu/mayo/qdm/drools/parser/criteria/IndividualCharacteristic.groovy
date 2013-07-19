package edu.mayo.qdm.drools.parser.criteria

import edu.mayo.qdm.drools.parser.TemporalProcessor
import edu.mayo.qdm.patient.Gender
import org.apache.commons.lang.BooleanUtils
import org.apache.commons.lang.StringUtils

import java.text.SimpleDateFormat;

/**
 */
class IndividualCharacteristic implements Criteria {

    def temporalProcessor = new TemporalProcessor()

    def resultString

    def dateFormat = new SimpleDateFormat()

    IndividualCharacteristic(json, measurementPeriod){
        def property = json.property;

        def droolsString;

        if(StringUtils.isEmpty(property)){
            if(json.definition.equals("patient_characteristic")){
                droolsString = this.handleGenericPatientCharacteristic(json)
            } else {
                throw new RuntimeException("Cannot determine Individual Characteristic for: JSON ->  $json")
            }
        } else {
            if(this.hasProperty(property)){
                droolsString = this."$property"(json, measurementPeriod)
            } else {
                throw new RuntimeException("Individual Characteristic `$property` not recognized.\n JSON ->  $json")
            }
        }

        if(BooleanUtils.toBoolean(json.negation)){
            this.resultString = "not( $droolsString )"
        } else {
            this.resultString = droolsString
        }
    }

    def handleGenericPatientCharacteristic(json){
        "/*TODO - Generic Patient Characteristic.*/"
    }

    def birthtime = { json, measurementPeriod ->
        temporalProcessor.processTemporalReferences(
                json.temporal_references,
                measurementPeriod,
                "birthdate",
                "birthdate"
        )
    }

    def gender = { json, measurementPeriod ->
        def genderValueSet = json.code_list_id

        switch (genderValueSet){
            case "2.16.840.1.113883.3.560.100.2": return "\$p.sex == ${Gender.name}.FEMALE"
            case "2.16.840.1.113883.3.560.100.1": return "\$p.sex == ${Gender.name}.MALE"
            default: throw new RuntimeException("Cannot determine Gender for JSON -> $json")
        }
    }

    def race = { json, measurementPeriod ->
        throw new RuntimeException(json.toString())
    }

    def ethnicity = { json, measurementPeriod ->
        "eval(true)"
    }

    def payer = { json, measurementPeriod ->
        "eval(true)"
    }

    def clinicalTrialParticipant = { json, measurementPeriod ->
        "eval(true)"
    }

    def expired = { json, measurementPeriod ->
        "eval(true)"
    }

    @Override
    def toDrools() {
        """( $resultString )"""
    }

    @Override
    def hasEventList(){
        false
    }

}
