package edu.mayo.qdm.executor.drools.parser.criteria
import edu.mayo.qdm.patient.Gender
import org.apache.commons.lang.StringUtils
/**
 */
class IndividualCharacteristic implements Criteria {

    def resultString

    def name

    def valueSetCodeResolver

    IndividualCharacteristic(fullJson, measurementPeriod){
        this.name = fullJson.key

        def json = fullJson.value
        def property = json.property

        def droolsString

        if(StringUtils.isEmpty(property)){
            throw new RuntimeException("Cannot handle generic Individual Characteristic here. The CriteriaFactory should process those. JSON ->  $json")
        } else {
            if(this.hasProperty(property)){
                droolsString = this."$property"(json, measurementPeriod)
            } else {
                throw new RuntimeException("Individual Characteristic `$property` not recognized.\n JSON ->  $json")
            }
        }

        this.resultString = droolsString
    }

    def gender = { json, measurementPeriod ->
        def genderValueSet = json.code_list_id

        switch (genderValueSet){
            case "2.16.840.1.113762.1.4.1": return "true"
            case "2.16.840.1.113883.3.560.100.2": return "\$p.sex == ${Gender.name}.FEMALE"
            case "2.16.840.1.113883.3.560.100.1": return "\$p.sex == ${Gender.name}.MALE"
            default: throw new RuntimeException("Cannot determine Gender for JSON -> $json")
        }
    }

    def race = { json, measurementPeriod ->
        "eval(true)"
    }

    def ethnicity = { json, measurementPeriod ->
        "eval(true)"
    }

    def payer = { json, measurementPeriod ->
        "eval(true)"
    }

    def clinicalTrialParticipant = { json, measurementPeriod ->
        """
        /* TODO: Cypress data doesn't have this, so I'm not sure how to represent it */
        eval(false)
        """
    }

    def expired = { json, measurementPeriod ->
        "eval(true)"
    }

    @Override
    def getLHS() {
        """
        \$p : Patient(
                ${this.resultString}
        )
        """
    }

    @Override
    def getRHS() {
        """
        insertLogical(new PreconditionResult("$name", \$p))
        """
    }
}
