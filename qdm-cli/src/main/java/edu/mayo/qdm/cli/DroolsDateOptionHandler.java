package edu.mayo.qdm.cli;

import edu.mayo.qdm.drools.DroolsDateFormat;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionDef;
import org.kohsuke.args4j.spi.OneArgumentOptionHandler;
import org.kohsuke.args4j.spi.Setter;

import java.text.ParseException;
import java.util.Date;

public class DroolsDateOptionHandler extends OneArgumentOptionHandler<Date> {

    private DroolsDateFormat droolsDateFormat = new DroolsDateFormat();

        public DroolsDateOptionHandler(CmdLineParser parser, OptionDef option, Setter<? super Date> setter) {
            super(parser, option, setter);
        }

        @Override
        protected Date parse(String argument) {
            try {
                return droolsDateFormat.parse(argument);
            } catch (ParseException e) {
                throw new RuntimeException("Date format is not recognized. Please use format: " + DroolsDateFormat.PATTERN);
            }
        }

    }