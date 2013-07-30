package edu.mayo.qdm.executor.drools;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.patient.Patient;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@Ignore
public class DroolsExecutorCompileTestIT extends AbstractAllMeasuresTestITBase {

    public DroolsExecutorCompileTestIT(XmlStream xml) {
        super(xml);
    }

    @Test
	public void TestExecute() throws IOException {
		
		Patient p1 = new Patient("12345");

		Patient p2 = new Patient("123456");

		Patient p3 = new Patient("123456");


		try {
			this.executor.execute(Arrays.asList(p1, p2, p3), IOUtils.toString(this.xml.xmlStream), MeasurementPeriod.getCalendarYear(new Date()));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(this.xml.name);
		}
	}

}