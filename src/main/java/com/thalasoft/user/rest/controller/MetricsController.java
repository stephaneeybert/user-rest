package com.thalasoft.user.rest.controller;

import java.util.HashMap;
import java.util.Map;

import com.thalasoft.user.rest.service.MetricsService;
import com.thalasoft.user.rest.utils.RESTConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponentsBuilder;

@Controller
@RequestMapping(RESTConstants.SLASH + RESTConstants.METRICS)
public class MetricsController {

  @Autowired
  private MetricsService metricsService;

  @GetMapping
  @ResponseBody
  public Map<String, HashMap<Integer, Integer>> getStatusMetric(UriComponentsBuilder builder) {
    return metricsService.getMetrics();
  }

  @GetMapping(value = RESTConstants.SLASH + RESTConstants.GRAPH)
  @ResponseBody
  public Object[][] getGraphData(UriComponentsBuilder builder) {
    return metricsService.getGraphData();
  }

}
