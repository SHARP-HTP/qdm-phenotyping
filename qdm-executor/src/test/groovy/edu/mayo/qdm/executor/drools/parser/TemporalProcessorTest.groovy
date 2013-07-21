package edu.mayo.qdm.executor.drools.parser

import edu.mayo.qdm.executor.MeasurementPeriod
import org.junit.Test

import static org.junit.Assert.*

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

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), "birthdate", "birthdate")

        assertEquals "birthdate <= '01-Jan-1948'".trim(), ivl.trim()

    }

    @Test
    void testLowAndHigh(){
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
                            ],
                            high: [
                                    type: "PQ",
                                    unit: "a",
                                    value: "18",
                                    "inclusive?": false,
                                    "derived?": false
                            ]
                    ]
            ]

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), "birthdate", "birthdate")


        assertTrue ivl.contains("birthdate > '01-Jan-1995'")
        assertTrue ivl.contains("birthdate <= '01-Jan-1948'")

    }
}
