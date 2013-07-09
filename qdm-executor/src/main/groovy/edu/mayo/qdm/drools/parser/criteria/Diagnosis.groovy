package edu.mayo.qdm.drools.parser.criteria
/**
 */
class Diagnosis implements Criteria {

    def json
    def valueSetCodeResolver

    @Override
    def toDrools() {
        def valueSetOid = json.code_list_id

        """
        droolsUtil.findMatches("$valueSetOid", \$p.getDiagnoses()).size() > 0
        """
    }
}
