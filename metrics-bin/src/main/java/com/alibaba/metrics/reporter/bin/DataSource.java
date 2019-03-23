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
package com.alibaba.metrics.reporter.bin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricObject.MetricType;

import java.util.Map;
import java.util.Map.Entry;

import static com.alibaba.metrics.utils.Constants.METRICS_SEPARATOR;
import static com.alibaba.metrics.utils.Constants.TAGS_SEPARATOR;
import static com.alibaba.metrics.utils.Constants.TAG_KV_SEPARATOR;
import static com.alibaba.metrics.utils.FigureUtil.getValueType;

public class DataSource {

	private String metricName;

	private Map<String, String> tags;

	private MetricType metricType;

	/** 度量器名称 */
	private String meterName;

	/** 字段类型 */
	private int fieldType;

	/** 归档类型 */
	private int archiveType;

	/** 缺失字段数量缺失的百分比，少于这个归档会被归为空 */
	private double absent;

	/** 字段序数 */
	private int order;

	/** 在解压后的数据块中字段的位置 */
	private int offset;

	/** 方便查询的metricobject */
	@JSONField(serialize=false)
	private MetricObject metricObject;

	/** 日志等级 */
	private MetricLevel level;

	/** 第一次出现的时间戳，查找的时候需要对比这个时间戳防止误读 */
	private long addTime;

	public DataSource(){

	}

	public String getMetricName() {
		return metricName;
	}

	public void setMetricName(String metricName) {
		this.metricName = metricName;
	}

	public Map<String, String> getTags() {
		return tags;
	}

	public void setTags(Map<String, String> tags) {
		this.tags = tags;
	}

	public MetricType getMetricType() {
		return metricType;
	}

	public void setMetricType(MetricType metricType) {
		this.metricType = metricType;
	}


	public int getArchiveType() {
		return archiveType;
	}

	public void setArchiveType(int archiveType) {
		this.archiveType = archiveType;
	}

	public double getAbsent() {
		return absent;
	}

	public void setAbsent(double absent) {
		this.absent = absent;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public MetricObject getMetricObject() {
		return metricObject;
	}

	public void setMetricObject(MetricObject metricObject) {
		this.metricObject = metricObject;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public MetricLevel getLevel() {
		return level;
	}

	public void setLevel(MetricLevel level) {
		this.level = level;
	}

	public int getFieldType() {
		return fieldType;
	}

	public void setFieldType(int fieldType) {
		this.fieldType = fieldType;
	}

	public long getAddTime() {
		return addTime;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

    public String getMeterName() {
        return meterName;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }

    public DataSource(MetricObject metricObject, MetricLevel level, int offset, int order, long metricTime, String meterName) {
		this(metricObject.getMetric(), metricObject.getTags(), metricObject.getMetricType(), 1,
				level, getValueType(metricObject.getValue()), 0.9, offset, order, metricTime, meterName);
	}

	public DataSource(String metricName, Map<String, String> tags, MetricType metricType, int archiveType,
			MetricLevel level, int fieldType, int offset, int order,long metricTime, String meterName) {
		this(metricName, tags, metricType, archiveType, level, fieldType, 0.9, offset, order, metricTime, meterName);
	}

	public DataSource(String metricName, Map<String, String> tags, MetricType metricType, int archiveType,
			MetricLevel level, int fieldType, double absent, int offset, int order, long addTime, String meterName) {
		this.metricName = metricName;
		this.tags = tags;
		this.metricType = metricType;
		this.archiveType = archiveType;
		this.fieldType = fieldType;
		this.level = level;
		this.absent = absent;
		this.offset = offset;
		this.order = order;
		this.addTime = addTime;
		this.meterName = meterName;

		this.metricObject = MetricObject.named(metricName).withType(metricType).withTimestamp(0L).withValue(0)
				.withLevel(level).withTags(tags).build();
	}

	public String getMetricsString() {
		if (tags == null || tags.isEmpty()) {
			return metricName;
		}

		int tagNum = 0;

		StringBuilder sb = new StringBuilder(100);
		sb.append(metricName);
		sb.append(METRICS_SEPARATOR);
		for (Entry<String, String> entry : tags.entrySet()) {
			sb.append(entry.getKey());
			sb.append(TAG_KV_SEPARATOR);
			sb.append(entry.getValue());
			tagNum = tagNum + 1;
			if (tagNum < tags.size()) {
				sb.append(TAGS_SEPARATOR);
			}
		}

		return sb.toString();
	}

	public void addMetricObject() {

		MetricObject metricObject = MetricObject.named(this.metricName).withTags(this.tags)
				.withLevel(this.level).withType(this.metricType).build();

		this.metricObject = metricObject;
	}

	public byte[] toJsonBytes() {
		return JSON.toJSONBytes(this, SerializerFeature.QuoteFieldNames);
	}

}
