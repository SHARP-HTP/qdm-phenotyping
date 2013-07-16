package edu.mayo.qdm.drools;

import java.text.SimpleDateFormat;

public class DroolsDateFormat extends SimpleDateFormat {

    public final static String PATTERN = "dd-MMM-yyyy";

    public DroolsDateFormat() {
        super(PATTERN);
    }
}
