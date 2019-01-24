package com.alibaba.metrics.rest;

import javax.ws.rs.core.MediaType;

public interface Constants {

    String DEFAULT_JSON_QUALITY_SOURCE = "0.1"; // the value should be between 0 and 1

    String PRODUCE_JSON_WITH_QUALITY_SOURCE = MediaType.APPLICATION_JSON + ";qs=" + DEFAULT_JSON_QUALITY_SOURCE;


}
