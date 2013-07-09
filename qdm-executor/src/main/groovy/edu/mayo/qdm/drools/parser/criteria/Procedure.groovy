package edu.mayo.qdm.drools.parser.criteria
/**
 */
class Procedure implements Criteria {

    def json
    def valueSetCodeResolver

    @Override
    def toDrools() {
        def valueSetOid = json.code_list_id

        """
        droolsUtil.findMatches("$valueSetOid", \$p.getProcedures()).size() > 0
        """
    }
}
