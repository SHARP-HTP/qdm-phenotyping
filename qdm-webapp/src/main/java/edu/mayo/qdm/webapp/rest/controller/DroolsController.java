package edu.mayo.qdm.webapp.rest.controller;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            @RequestParam String measureid) throws Exception {
        return this.toDrools(response, measureid);
    }

    @RequestMapping(value = "/qdm2drools/{measureId}", method = RequestMethod.GET)
    public ResponseEntity<?> toDrools(
        HttpServletResponse response,
        @PathVariable String measureId) throws Exception {
        response.setContentType("text/plain");

        Map<String,String> params = new HashMap<String,String>();
        params.put("format", "hqmf");
        params.put("measureId", measureId);

        String qdmXml = (String) this.restClient.GET(USHIK_REST_URL, "application/xml", ContentType.TEXT, false, params);

        return this.doGetDrools(qdmXml);
    }

    @RequestMapping(value = "/qdm/drools", method= RequestMethod.POST)
    public ResponseEntity<?> toDroolsFromFile(
            HttpServletRequest request) throws Exception {

        if(! (request instanceof MultipartHttpServletRequest)){
            throw new IllegalStateException("ServletRequest expected to be of type MultipartHttpServletRequest");
        }

        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = multipartRequest.getFile("qdm");

        String qdmXml = IOUtils.toString(multipartFile.getInputStream());

        return this.doGetDrools(qdmXml);
    }

    protected ResponseEntity<String> doGetDrools(String qdmXml){
        String drools;
        try {
            drools = this.qdm2Drools.qdm2drools(qdmXml);
        } catch (Exception e){
            throw new DroolsException(e);
        }

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<String>(drools, responseHeaders, HttpStatus.OK);
    }

    @ExceptionHandler(DroolsException.class)
    public ModelAndView handleDroolsException(Exception ex) {

        ModelAndView model = new ModelAndView("error");
        model.addObject("error", "Error translating to Drools: " + ex.getMessage());

        return model;
    }

    private static class DroolsException extends RuntimeException {
        private DroolsException(Exception cause) {
            super(cause);
        }
    }

}
