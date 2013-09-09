package edu.mayo.qdm.executor.drools.parser
import edu.mayo.qdm.executor.MeasurementPeriod
import org.junit.Test

import static org.junit.Assert.assertTrue
/**
 */
class TemporalProcessorTest {

    @Test
    void testOnlyLow(){
        def proc = new TemporalProcessor()

        def temporalReferences =
                [
                    type: "SBS",
                    reference: "MeasurePeriod",
                    range: [
                        type: "IVL_PQ",
                        low: [
                            type: "PQ",
                            unit: "a",
                            value: "65",
                            "inclusive?": true,
                            "derived?": false
                        ]
                    ]
                ]

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), null, "birthdate", "birthdate")

        println ivl.criteria

        assertTrue ivl.criteria.contains("toDays(birthdate) < toDays(new Date('01-Jan-1948'))")

    }

}
