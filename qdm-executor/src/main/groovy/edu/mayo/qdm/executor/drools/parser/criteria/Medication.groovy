package edu.mayo.qdm.executor.drools.parser.criteria
/**
 */
class Medication {

    @Override
    def getName() {
        "Medication"
    }

    @Override
    def getCriteria() {
        """medicationStatus == ${toMedicationStatus(this.json.value.status)}"""
    }

    def toMedicationStatus(jsonStatus){
        switch(jsonStatus){
            case "active" : return "MedicationStatus.ACTIVE"
            case "ordered" : return "MedicationStatus.ORDERED"
            case "administered" : return "MedicationStatus.ADMINISTERED"
            case "dispensed" : return "MedicationStatus.DISPENSED"
            default: throw new RuntimeException("""Json Medication Status: $jsonStatus not recognized.""")
        }
    }
}
