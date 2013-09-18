package edu.mayo.qdm.webapp.rest.controller;

import edu.mayo.qdm.executor.MeasurementPeriod;
import edu.mayo.qdm.executor.drools.DroolsDateFormat;
import edu.mayo.qdm.executor.drools.parser.Qdm2Drools;
import edu.mayo.qdm.webapp.client.NlmEmeasureParser;
import edu.mayo.qdm.webapp.client.RestClient;
import groovyx.net.http.ContentType;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
public class DroolsController implements InitializingBean {

    private Qdm2Drools qdm2Drools;

    private RestClient restClient = new RestClient();

    private NlmEmeasureParser nlmEmeasureParser = new NlmEmeasureParser();

    private DroolsDateFormat droolsDateFormat = new DroolsDateFormat();

    private Object emeasures;

    private static final String NLM_REST_URL = "https://vsac.nlm.nih.gov/vsac/pc/measure/cmsids";
    private static final String USHIK_REST_URL = "https://ushik.ahrq.gov/rest/meaningfulUse/retrieveHQMFXML";

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassPathXmlApplicationContext context =
            new ClassPathXmlApplicationContext("qdm-executor-context.xml");

        context.registerShutdownHook();

        this.qdm2Drools = context.getBean(Qdm2Drools.class);

        this.emeasures = this.nlmEmeasureParser.parseEmeasureJson(this.restClient.GET(NLM_REST_URL));
    }

    @RequestMapping(value = "/qdm2drools", method = RequestMethod.GET, params = "!measureid")
    public ModelAndView getQdm2DroolsHome(){
        ModelAndView modelAndView = new ModelAndView("qdm2drools");

        modelAndView.addObject("emeasures", this.emeasures);

        return modelAndView;
    }

    @RequestMapping(value = "/qdm2drools", method = RequestMethod.GET, params = "measureid")
    public ResponseEntity<?> getQdm2DroolsHome(
            HttpServletResponse response,
            @RequestParam String measureid,
            @RequestParam(required=false) String effectiveDate) throws Exception {
        return this.toDrools(response, measureid, effectiveDate);
    }

    @RequestMapping(value = "/qdm2drools/{measureId}", method = RequestMethod.GET)
    public ResponseEntity<?> toDrools(
        HttpServletResponse response,
        @PathVariable String measureId,
        @RequestParam(required=false) String effectiveDate) throws Exception {
        response.setContentType("text/plain");

        Date date;
        if(effectiveDate == null){
            date = new Date();
        } else {
            date = this.droolsDateFormat.parse(effectiveDate);
        }

        Map<String,String> params = new HashMap<String,String>();
        params.put("format", "hqmf");
        params.put("measureId", measureId);

        String qdmXml = (String) this.restClient.GET(USHIK_REST_URL, "application/xml", ContentType.TEXT, false, params);

        return this.doGetDrools(qdmXml, date);
    }

    @RequestMapping(value = "/qdm/drools", method= RequestMethod.POST)
    public ResponseEntity<?> toDroolsFromFile(
            HttpServletRequest request,
            @RequestParam String effectiveDate) throws Exception {

        if(! (request instanceof MultipartHttpServletRequest)){
            throw new IllegalStateException("ServletRequest expected to be of type MultipartHttpServletRequest");
        }

        Date date;
        if(effectiveDate == null){
            date = new Date();
        } else {
            date = this.droolsDateFormat.parse(effectiveDate);
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("qdm");

        String qdmXml = IOUtils.toString(multipartFile.getInputStream());

        return this.doGetDrools(qdmXml, date);
    }

    protected ResponseEntity<String> doGetDrools(String qdmXml, Date date){
        String drools = this.qdm2Drools.qdm2drools(qdmXml, MeasurementPeriod.getCalendarYear(date));

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<String>(drools, responseHeaders, HttpStatus.OK);
    }

}
