package edu.mayo.qdm.cli;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.drools.parser.Qdm2Drools;
import org.apache.commons.io.FileUtils;
import org.kohsuke.args4j.Option;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Command line utility for producing Drools Rules from QDM XML.
 */
public class GenerateDroolsRulesLauncher extends AbstractBaseCliLauncher {

    @Option(name="-f", required=true, usage="QDM XML input file")
    private File xml;

    @Option(name="-d", handler=DroolsDateOptionHandler.class, usage="Effective measurement date")
    private Date date = new Date();

    public static void main(String[] args) throws IOException {
        new GenerateDroolsRulesLauncher().doMain(args);
    }

    @Override
    protected void run() throws Exception {
        ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("qdm-executor-context.xml");

        context.registerShutdownHook();

        Qdm2Drools qdm2Drools = context.getBean(Qdm2Drools.class);

        String drools =
            qdm2Drools.qdm2drools(FileUtils.readFileToString(this.xml), MeasurementPeriod.getCalendarYear(this.date));

        System.out.println(drools);
    }
}
