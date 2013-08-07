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

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), null, "birthdate", "birthdate")

        println ivl.criteria

        assertTrue ivl.criteria.contains("birthdate <= new Date('01-Jan-1948')")

    }

    @Test
    void testLowAndHighSBS(){
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
                                    value: "18",
                                    "inclusive?": true,
                                    "derived?": false
                            ],
                            high: [
                                    type: "PQ",
                                    unit: "a",
                                    value: "65",
                                    "inclusive?": false,
                                    "derived?": false
                            ]
                    ]
            ]

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), null, "birthdate", "birthdate")

        println ivl.criteria

        assertTrue ivl.criteria.contains("birthdate > new Date('01-Jan-1948')")
        assertTrue ivl.criteria.contains("birthdate <= new Date('01-Jan-1995')")

    }

    @Test
    void testLowAndHighEBE(){
        def proc = new TemporalProcessor()

        def temporalReferences =
            [
                    type: "EBE",
                    reference: "MeasurePeriod",
                    range: [
                            type: "IVL_PQ",
                            high: [
                                    type: "PQ",
                                    unit: "mo",
                                    value: "24",
                                    "inclusive?": true,
                                    "derived?": false
                            ],
                            low: [
                                    type: "PQ",
                                    unit: "mo",
                                    value: "12",
                                    "inclusive?": true,
                                    "derived?": false
                            ]
                    ]
            ]

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), null, "start", "end")

        println ivl.criteria

        assertTrue ivl.criteria.contains("end >= new Date('31-Dec-2011')")
        assertTrue ivl.criteria.contains("end <= new Date('31-Dec-2012')")
    }

    @Test
    void testLowAndHighEAE(){
        def proc = new TemporalProcessor()

        def temporalReferences =
            [
                    type: "EAE",
                    reference: "MeasurePeriod",
                    range: [
                            type: "IVL_PQ",
                            high: [
                                    type: "PQ",
                                    unit: "mo",
                                    value: "24",
                                    "inclusive?": true,
                                    "derived?": false
                            ],
                            low: [
                                    type: "PQ",
                                    unit: "mo",
                                    value: "12",
                                    "inclusive?": true,
                                    "derived?": false
                            ]
                    ]
            ]

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), null, "start", "end")

        println ivl.criteria

        assertTrue ivl.criteria.contains("end >= new Date('31-Dec-2014')")
        assertTrue ivl.criteria.contains("end <= new Date('31-Dec-2015')")
    }

    @Test
    void testHighEBE(){
        def proc = new TemporalProcessor()

        def temporalReferences =
            [
                    type: "EBE",
                    reference: "MeasurePeriod",
                    range: [
                            type: "IVL_PQ",
                            high: [
                                    type: "PQ",
                                    unit: "a",
                                    value: "2",
                                    "inclusive?": true,
                                    "derived?": false
                            ]
                    ]
            ]

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), null, "start", "end")

        println ivl.criteria

        assertTrue ivl.criteria.contains("end >= new Date('31-Dec-2011')")
    }

    @Test
    void testLowAndHighSAE(){
        def proc = new TemporalProcessor()

        def temporalReferences =
            [
                    type: "SAE",
                    reference: "MeasurePeriod",
                    range: [
                            type: "IVL_PQ",
                            high: [
                                    type: "PQ",
                                    unit: "mo",
                                    value: "24",
                                    "inclusive?": true,
                                    "derived?": false
                            ],
                            low: [
                                    type: "PQ",
                                    unit: "mo",
                                    value: "12",
                                    "inclusive?": true,
                                    "derived?": false
                            ]
                    ]
            ]

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), null, "start", "end")

        println ivl.criteria

        assertTrue ivl.criteria.contains("start >= new Date('31-Dec-2014')")
        assertTrue ivl.criteria.contains("start <= new Date('31-Dec-2015')")
    }

    @Test
    void testLowAndHighNonMeasurementPeriodSAE(){
        def proc = new TemporalProcessor()

        def temporalReferences =
            [
                    type: "SAE",
                    reference: "some_other_event",
                    range: [
                            type: "IVL_PQ",
                            high: [
                                    type: "PQ",
                                    unit: "mo",
                                    value: "24",
                                    "inclusive?": true,
                                    "derived?": false
                            ],
                            low: [
                                    type: "PQ",
                                    unit: "mo",
                                    value: "12",
                                    "inclusive?": true,
                                    "derived?": false
                            ]
                    ]
            ]

        def ivl = proc.processTemporalReference(temporalReferences, MeasurementPeriod.getCalendarYear(new Date()), null, "start", "end")

        println ivl.criteria

        assertTrue ivl.criteria.contains("start <= droolsUtil.add(droolsUtil.getCalendar(\$some_other_event.event.endDate), Calendar.MONTH, 24)")
        assertTrue ivl.criteria.contains("start >= droolsUtil.add(droolsUtil.getCalendar(\$some_other_event.event.endDate), Calendar.MONTH, 12)")
    }
}
