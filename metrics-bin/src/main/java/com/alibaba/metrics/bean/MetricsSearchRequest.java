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

