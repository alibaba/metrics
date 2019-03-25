/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.metrics.bean;

import com.alibaba.metrics.common.MetricObject.MetricType;

import java.util.Map;

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
