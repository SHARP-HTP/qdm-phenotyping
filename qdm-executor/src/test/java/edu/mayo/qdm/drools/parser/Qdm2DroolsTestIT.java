package edu.mayo.qdm.drools.parser;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.IOException;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:/qdm-executor-context.xml")
public class Qdm2DroolsTestIT {

    @Resource
    private Qdm2Drools qdm2Drools;

    @Test
    public void testCreateRules127() throws IOException {
        String rule = qdm2Drools.qdm2drools(
            IOUtils.toString(
                new ClassPathResource("qdmxml/CMS135v1.xml").getInputStream()));

        assertNotNull(rule);

        System.out.print(rule);
    }

    @Test
    public void testCreateRules124() throws IOException {
        String rule = qdm2Drools.qdm2drools(
                IOUtils.toString(
                        new ClassPathResource("qdmxml/CMS124v1.xml").getInputStream()));

        assertNotNull(rule);

        System.out.print(rule);
    }

    @Test
    public void testCreateRules117() throws IOException {
        String rule = qdm2Drools.qdm2drools(
                IOUtils.toString(
                    new ClassPathResource("qdmxml/CMS117v1.xml").getInputStream()));

        assertNotNull(rule);

        System.out.print(rule);
    }
}
