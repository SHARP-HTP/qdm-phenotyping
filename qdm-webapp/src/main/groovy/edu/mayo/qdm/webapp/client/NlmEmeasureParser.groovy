package edu.mayo.qdm.webapp.client

import net.sf.json.JSONNull

/**
 */
class NlmEmeasureParser {

    def parseEmeasureJson(emeasureJson){
        def emeasures = []
        emeasureJson.rows.each {
            emeasures.add(
                    new Emeasure(
                            measureId: it.measureid,
                            nqfId: it.nqf instanceof JSONNull ? null : it.nqf,
                            title: it.title))
        }

        emeasures
    }
}
