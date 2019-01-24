package com.alibaba.metrics.bean;

import java.util.List;

public class MetricsSearchResponse {

	private long startTime;

	private long endTime;

	private List<MetricResult> result;

	private MetricsDataStatus dataStatus;

	private MetricsRecordStatus recordStatus;

	private String msg;

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

	public List<MetricResult> getResult() {
		return result;
	}

	public void setResult(List<MetricResult> result) {
		this.result = result;
	}

	public MetricsDataStatus getDataStatus() {
		return dataStatus;
	}

	public void setDataStatus(MetricsDataStatus dataStatus) {
		this.dataStatus = dataStatus;
	}

	public MetricsRecordStatus getRecordStatus() {
		return recordStatus;
	}

	public void setRecordStatus(MetricsRecordStatus recordStatus) {
		this.recordStatus = recordStatus;
	}

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
