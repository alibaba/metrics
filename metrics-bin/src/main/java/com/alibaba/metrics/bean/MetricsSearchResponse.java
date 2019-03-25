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
