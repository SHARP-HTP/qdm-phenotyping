package edu.mayo.qdm.drools.parser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-executor-context.xml")
public class Qdm2DroolsTestIT {

    @Resource
    private Qdm2Drools qdm2Drools;

    @Test
    public void testCreateRules127() throws IOException {

    }

    @Test
    public void testCreateRules124() throws IOException {

    }

    @Test
    public void testCreateRules117() throws IOException {

    }
}
