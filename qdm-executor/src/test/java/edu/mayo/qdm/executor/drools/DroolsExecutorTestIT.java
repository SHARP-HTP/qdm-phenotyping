package edu.mayo.qdm.executor.drools;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-executor-context.xml")
public class DroolsExecutorTestIT {

	@Autowired
	private DroolsExecutor executor;
	
	@Test
	public void TestSetUp(){
		Assert.assertNotNull(this.executor);
	}

}
