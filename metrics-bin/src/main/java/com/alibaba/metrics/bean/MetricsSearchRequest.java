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

import java.util.List;

public class MetricsSearchRequest {

	private long startTime;

	private long endTime;

	private int limit;

	private int precision;

	private MetricSource source = MetricSource.CURRENT;

	private List<MetricSearch> queries;

	/**
	 * 当统计值为0时，zeroIgnore = true会过滤掉这些项
	 */
	private boolean zeroIgnore = false;

	public MetricsSearchRequest(){

	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public List<MetricSearch> getQueries() {
		return queries;
	}

	public void setQueries(List<MetricSearch> queries) {
		this.queries = queries;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

    public MetricSource getSource() {
        return source;
    }

    public void setSource(MetricSource source) {
        this.source = source;
    }

    public boolean isZeroIgnore() {
        return zeroIgnore;
    }

    public void setZeroIgnore(boolean zeroIgnore) {
        this.zeroIgnore = zeroIgnore;
    }

}

