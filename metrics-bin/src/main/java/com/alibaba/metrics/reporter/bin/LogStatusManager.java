package com.alibaba.metrics.reporter.bin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.StringUtils;
import com.alibaba.metrics.bean.MetricSearch;
import com.alibaba.metrics.common.MetricObject;

public class LogStatusManager {

	private volatile boolean enable = false;

	private Map<MetricLevel, NavigableMap<Long, IndexData>> indexData = new HashMap<MetricLevel, NavigableMap<Long, IndexData>>();

	private Map<MetricObject, DataSource> dataSources = new HashMap<MetricObject, DataSource>();

	private Map<MetricLevel, Long> lastCollectionTime = new HashMap<MetricLevel, Long>(){{
		put(MetricLevel.CRITICAL, 0L);
		put(MetricLevel.MAJOR, 0L);
		put(MetricLevel.MINOR, 0L);
		put(MetricLevel.NORMAL, 0L);
		put(MetricLevel.TRIVIAL, 0L);
	}};

	public void addIndex(final long timestamp, MetricLevel level, final long start, final long end) {

		SortedMap<Long, IndexData> index = indexData.get(level);

		if (index == null) {
			indexData.put(level, new TreeMap<Long, IndexData>() {
				{
					put(timestamp, new IndexData(start, end));
				}
			});
		} else {
			index.put(timestamp, new IndexData(start, end));
		}

	}

	public void addDataSources(MetricObject metricObject, DataSource dataSource) {
		dataSources.put(dataSource.getMetricObject(), dataSource);
	}

	public void addDataSources(Map<MetricObject, DataSource> dataSources) {
		this.dataSources.putAll(dataSources);
	}

	public DataSource getDataSource(MetricObject metricObject) {
		return dataSources.get(metricObject);
	}

	public Map<MetricLevel, Set<DataSource>> search(List<MetricSearch> metricNames) {

		if (metricNames == null || metricNames.size() == 0) {
			return null;
		}

		Map<MetricLevel, Set<DataSource>> result = new HashMap<MetricLevel, Set<DataSource>>();

		for (MetricSearch simpleMetric : metricNames) {

			String key = simpleMetric.getKey();
			Map<String, String> tags = simpleMetric.getTags();

			for (Entry<MetricObject, DataSource> entry0 : dataSources.entrySet()) {

				DataSource dataSource = entry0.getValue();
				String metricObjectName = dataSource.getMetricName();

				boolean keysEqual = true;
				boolean tagsEqual = true;

				if (!StringUtils.equals(key, metricObjectName)) {
					keysEqual = false;
					continue;
				}

				if (tags != null && tags.size() > 0) {

					Map<String, String> dataSourceTag = dataSource.getTags();

					if (dataSourceTag == null || dataSourceTag.size() == 0) {
						tagsEqual = false;
					} else {
						for (Entry<String, String> entry : tags.entrySet()) {

							String entryKey = entry.getKey();
							String entryValue = entry.getValue();

							if (dataSourceTag.containsKey(entryKey)) {

								// *号代表这个包含这个tagname的tag都匹配
								if ("*".equals(entryValue)) {
									continue;
								} else {

									if (entryValue == null){
										continue;
									}
									String[] metricTagValues = entryValue.split("\\|");
									String dataSourceValue = dataSourceTag.get(entryKey);

									boolean orMatch = false;

									for (String tag : metricTagValues) {

										if (StringUtils.equals(tag, dataSourceValue)) {
											orMatch = true;
											break;
										}

									}

									if (!orMatch) {
										tagsEqual = false;
									}
								}

							} else {
								tagsEqual = false;
								break;
							}

						}

					}

				}

				if (keysEqual && tagsEqual) {
					addSearchResult(dataSource, result);
				}

			}
		}
		return result;
	}

	private void addSearchResult(DataSource dataSource, Map<MetricLevel, Set<DataSource>> result) {

		MetricLevel level = dataSource.getLevel();

		Set<DataSource> dataSourceSet = result.get(level);

		if (dataSourceSet == null) {
			dataSourceSet = result.put(level, new HashSet<DataSource>());
			dataSourceSet = result.get(level);
		}

		dataSourceSet.add(dataSource);

	}

	public SortedMap<Long, IndexData> searchIndex(long start, long end, MetricLevel level) {

		NavigableMap<Long, IndexData> indexMap = indexData.get(level);

		if (indexMap == null || indexMap.size() == 0) {
			return null;
		}

		NavigableMap<Long, IndexData> indexResult = indexMap.subMap(start, true, end, true);

		return indexResult;
	}

	public void clear() {
		// this.enable = false;
		indexData = new HashMap<MetricLevel, NavigableMap<Long, IndexData>>();
		dataSources = new HashMap<MetricObject, DataSource>();
	}

	public int getDataSourceNum() {
		return dataSources.size();
	}

	public long getLastCollectionTime(MetricLevel level) {
		return lastCollectionTime.get(level);
	}

	public void setLastCollectionTime(MetricLevel level, long lastCollectionTime) {
		this.lastCollectionTime.put(level, lastCollectionTime);
	}

}
