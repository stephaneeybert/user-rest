package com.thalasoft.user.rest.service;

import java.util.HashMap;
import java.util.Map;

public interface MetricsService {

  public void increaseCount(String request, int status);

  public Map<Integer, Integer> getStatusMetric();

  public Map<String, HashMap<Integer, Integer>> getMetrics();

  public Object[][] getGraphData();

}
