package edu.mayo.qdm.webapp.rest.controller;

import edu.mayo.qdm.MeasurementPeriod;
import edu.mayo.qdm.drools.DroolsDateFormat;
import edu.mayo.qdm.drools.parser.Qdm2Drools;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class DroolsController implements InitializingBean {

    private Qdm2Drools qdm2Drools;

    private DroolsDateFormat droolsDateFormat = new DroolsDateFormat();

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("qdm-executor-context.xml");

        context.registerShutdownHook();

        this.qdm2Drools = context.getBean(Qdm2Drools.class);
    }

    @RequestMapping(value = "/qdm/{qdmId}/drools", method= RequestMethod.GET)
    @ResponseBody
    public String toDrools(
        HttpServletRequest request, @PathVariable String qdmId) throws Exception {

        //TODO
        return null;
    }

    @RequestMapping(value = "/qdm/drools", method= RequestMethod.POST)
    @ResponseBody
    public String toDroolsFromFile(
            HttpServletRequest request,
            @RequestParam String effectiveDate) throws Exception {

        if(! (request instanceof MultipartHttpServletRequest)){
            throw new IllegalStateException("ServletRequest expected to be of type MultipartHttpServletRequest");
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("qdm");

        String xml = IOUtils.toString(multipartFile.getInputStream());

        Date date;
        if(effectiveDate == null){
            date = new Date();
        } else {
            date = this.droolsDateFormat.parse(effectiveDate);
        }

        return this.qdm2Drools.qdm2drools(xml, MeasurementPeriod.getCalendarYear(date));
    }

}
