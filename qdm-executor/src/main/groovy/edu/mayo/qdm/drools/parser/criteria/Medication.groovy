package edu.mayo.qdm.drools.parser.criteria
/**
 */
class Medication implements Criteria {

    def json
    def valueSetCodeResolver

    @Override
    def toDrools() {
        def valueSetOid = json.code_list_id

        """
        droolsUtil.findMatches("$valueSetOid", \$p.getMedications()).size() > 0
        """
    }
}
