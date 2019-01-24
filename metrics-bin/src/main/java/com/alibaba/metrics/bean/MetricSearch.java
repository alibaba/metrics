package com.alibaba.metrics.bean;

import java.util.Map;

public class MetricSearch {

    private String key;
    private Map<String, String> tags;

    public MetricSearch(){

    }

    public MetricSearch(String key, Map<String, String> tags){
    	this.key = key;
    	this.tags = tags;
    }
	public String getKey() {
		return key;
	}
	public Map<String, String> getTags() {
		return tags;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}


}
