package edu.mayo.qdm;

import edu.mayo.qdm.drools.parser.criteria.Interval;
import edu.mayo.qdm.drools.parser.criteria.MeasurementValue;
import org.joda.time.DateTime;

import java.util.Date;

/**
 */
public class MeasurementPeriod extends Interval {

    public MeasurementPeriod(Date start, boolean startInclusive, Date end, boolean endInclusive) {
        this(new MeasurementValue(Long.toString(start.getTime()), "TS", startInclusive),
                new MeasurementValue(Long.toString(end.getTime()), "TS", endInclusive));
    }

    public MeasurementPeriod(MeasurementValue lowValue, MeasurementValue highValue) {
        super(lowValue, highValue);
    }


    public static MeasurementPeriod getCalendarYear(Date effectiveDate) {
        DateTime start = new DateTime(effectiveDate);
        start = start.dayOfYear().withMinimumValue();

        DateTime end = new DateTime(effectiveDate);
        end = end.dayOfYear().withMaximumValue();

        return new MeasurementPeriod(start.toDate(), true, end.toDate(), true);
    }

}
