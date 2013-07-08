package edu.mayo.qdm.cli;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 */
public abstract class AbstractBaseCliLauncher {

    public void doMain(String[] args) {
        CmdLineParser parser = new CmdLineParser(this);

        // if you have a wider console, you could increase the value;
        // here 80 is also the default
        parser.setUsageWidth(80);

        try {
            // parse the arguments.
            parser.parseArgument(args);
        } catch( CmdLineException e ) {
            System.err.print(e.getMessage());
        }

        try {
            this.run();
        } catch( Exception e ) {
            System.err.print(e.getMessage());
        }
    }

    protected abstract void run() throws Exception;

}
