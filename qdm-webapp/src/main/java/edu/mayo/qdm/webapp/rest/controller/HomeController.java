package edu.mayo.qdm.webapp.rest.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

    private static final String DEFAULT_QDM2JSON_URL = "http://qdm2json.phenotypeportal.org/";

    @Value("${webapp.qdm2jsonUrl:"+DEFAULT_QDM2JSON_URL+"}")
    private String qdm2jsonUrl;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView getHomePage(){
        ModelAndView home = new ModelAndView("home");

        home.addObject("qdm2jsonUrl", qdm2jsonUrl);

        return home;
    }

}
