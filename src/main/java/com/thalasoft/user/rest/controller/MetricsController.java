package com.thalasoft.user.rest.controller;

import com.thalasoft.user.rest.service.MetricsService;
import com.thalasoft.user.rest.utils.RESTConstants;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH + RESTConstants.METRICS)
public class MetricsController {

    @Autowired 
	private MetricsService metricsService;
    
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, HashMap<Integer, Integer>> getStatusMetric(UriComponentsBuilder builder) {
    	return metricsService.getMetrics();
    }

    @RequestMapping(value = RESTConstants.SLASH + RESTConstants.GRAPH, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Object[][] getGraphData(UriComponentsBuilder builder) {
        return metricsService.getGraphData();
    }

}
