package com.alibaba.metrics.bean;

import java.util.Map;

import com.alibaba.metrics.common.MetricObject.MetricType;

public class MetricResult {

	private long timestamp;

	private String metricName;

	private ValueStatus valueStatus;

	private Map<String, String> tags;

	private Object value;

	private int precision;
	
	private MetricType metricType;
	
	private String meterName;
	
	public MetricResult(String metricName, Map<String, String> tags, ValueStatus valueStatus, Object value,
			long timestamp, int precision, MetricType metricType, String meterName) {
		
		this.metricName = metricName;
		this.tags = tags;
		this.valueStatus = valueStatus;
		this.value = value;
		this.timestamp = timestamp;
		this.precision = precision;
		this.metricType = metricType;
		this.meterName = meterName;
		
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public ValueStatus getValueStatus() {
		return valueStatus;
	}

	public void setValueStatus(ValueStatus valueStatus) {
		this.valueStatus = valueStatus;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
    public MetricType getMetricType() {
        return metricType;
    }

    public void setMetricType(MetricType metricType) {
        this.metricType = metricType;
    }

    public String getMeterName() {
        return meterName;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }
	
}