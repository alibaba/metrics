package com.alibaba.metrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

public class AliMetricManager implements IMetricManager {

    @SuppressWarnings("rawtypes")
    final private static SortedMap emptySortedMap = new TreeMap();

    private Map<String, MetricRegistry> metricRegistryMap;

    private volatile boolean enabled;

    public AliMetricManager() {
        metricRegistryMap = new ConcurrentHashMap<String, MetricRegistry>();
        enabled = true;
    }

    @Override
    public Meter getMeter(String group, MetricName name) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getMeter(group, name);
        }
        return getMetricRegistryByGroup(group).meter(name);
    }

    @Override
    public Counter getCounter(String group, MetricName name) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getCounter(group, name);
        }
        return getMetricRegistryByGroup(group).counter(name);
    }

    @Override
    public Histogram getHistogram(String group, MetricName name) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getHistogram(group, name);
        }
        return getMetricRegistryByGroup(group).histogram(name);
    }

    @Override
    public Histogram getHistogram(String group, MetricName name, ReservoirType type) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getHistogram(group, name, type);
        }
        return getMetricRegistryByGroup(group).histogram(name, type);
    }

    @Override
    public Timer getTimer(String group, MetricName name) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getTimer(group, name);
        }
        return getMetricRegistryByGroup(group).timer(name);
    }

    @Override
    public Timer getTimer(String group, MetricName name, ReservoirType type) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getTimer(group, name, type);
        }
        return getMetricRegistryByGroup(group).timer(name, type);
    }

    @Override
    public Compass getCompass(String group, MetricName name) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getCompass(group, name);
        }
        return getMetricRegistryByGroup(group).compass(name);
    }

    @Override
    public Compass getCompass(String group, MetricName name, ReservoirType type) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getCompass(group, name, type);
        }
        return getMetricRegistryByGroup(group).compass(name, type);
    }

    @Override
    public FastCompass getFastCompass(String group, MetricName name) {
        if (!this.enabled) {
            return MetricManager.NOP_METRIC_MANAGER.getFastCompass(group, name);
        }
        return getMetricRegistryByGroup(group).fastCompass(name);
    }

    @Override
    public List<String> listMetricGroups() {
        if (!this.enabled) {
            return Collections.emptyList();
        }
        List<String> groups = new ArrayList<String>();
        groups.addAll(metricRegistryMap.keySet());
        return groups;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Map<String, Set<MetricName>> listMetricNamesByGroup() {
        if (!this.enabled) {
            return Collections.emptyMap();
        }
        Map<String, Set<MetricName>> result = new HashMap<String, Set<MetricName>>();
        for (Map.Entry<String, MetricRegistry> entry : metricRegistryMap.entrySet()) {
            Set<MetricName> metricNames = new TreeSet<MetricName>();
            for (Map.Entry<MetricName, Metric> metricEntry : entry.getValue().getMetrics().entrySet()) {
                metricNames.add(metricEntry.getKey());
            }
            result.put(entry.getKey(), metricNames);
        }
        return result;
    }

    @Override
    public MetricRegistry getMetricRegistryByGroup(String group) {
        if (!metricRegistryMap.containsKey(group)) {
            metricRegistryMap.put(group, new MetricRegistryImpl());
        }
        return metricRegistryMap.get(group);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Gauge> getGauges(String group, MetricFilter filter) {
        if (!this.enabled) {
            return emptySortedMap;
        }
        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return emptySortedMap;
        }
        return metricRegistry.getGauges(filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Counter> getCounters(String group, MetricFilter filter) {
        if (!this.enabled) {
            return emptySortedMap;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return emptySortedMap;
        }
        return metricRegistry.getCounters(filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Histogram> getHistograms(String group, MetricFilter filter) {
        if (!this.enabled) {
            return emptySortedMap;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return emptySortedMap;
        }
        return metricRegistry.getHistograms(filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Meter> getMeters(String group, MetricFilter filter) {
        if (!this.enabled) {
            return emptySortedMap;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return emptySortedMap;
        }
        return metricRegistry.getMeters(filter);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SortedMap<MetricName, Timer> getTimers(String group, MetricFilter filter) {
        if (!this.enabled) {
            return emptySortedMap;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return emptySortedMap;
        }
        return metricRegistry.getTimers(filter);
    }

    @Override
    public SortedMap<MetricName, Compass> getCompasses(String group, MetricFilter filter) {
        if (!this.enabled) {
            return emptySortedMap;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return emptySortedMap;
        }
        return metricRegistry.getCompasses(filter);
    }

    @Override
    public SortedMap<MetricName, FastCompass> getFastCompasses(String group, MetricFilter filter) {
        if (!this.enabled) {
            return emptySortedMap;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        if (metricRegistry == null) {
            return emptySortedMap;
        }
        return metricRegistry.getFastCompasses(filter);
    }

    @Override
    public void register(String group, MetricName name, Metric metric) {
        if (!this.enabled) {
            return;
        }

        MetricRegistry metricRegistry = this.getMetricRegistryByGroup(group);
        metricRegistry.register(name, metric);
    }

    @Override
    public Map<MetricName, Metric> getMetrics(String group) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        MetricRegistry metricRegistry = this.metricRegistryMap.get(group);
        if (metricRegistry != null) {
            return metricRegistry.getMetrics();
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getCategoryMetrics(String group) {
        return getCategoryMetrics(group, MetricFilter.ALL);
    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getCategoryMetrics(String group,
            MetricFilter filter) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        MetricRegistry metricRegistry = this.metricRegistryMap.get(group);
        Map<MetricName, Metric> metrics = metricRegistry.getMetrics();
        return getCategoryMetrics(metrics, filter);
    }

    @Override
    public Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getAllCategoryMetrics(MetricFilter filter) {

        if (!this.enabled) {
            return Collections.emptyMap();
        }

        Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> result = new HashMap<Class<? extends Metric>, Map<MetricName, ? extends Metric>>();

        Map<MetricName, Gauge> gauges = new HashMap<MetricName, Gauge>();
        Map<MetricName, Counter> counters = new HashMap<MetricName, Counter>();
        Map<MetricName, Histogram> histograms = new HashMap<MetricName, Histogram>();
        Map<MetricName, Meter> meters = new HashMap<MetricName, Meter>();
        Map<MetricName, Timer> timers = new HashMap<MetricName, Timer>();
        Map<MetricName, Compass> compasses = new HashMap<MetricName, Compass>();
        Map<MetricName, FastCompass> fastCompasses = new HashMap<MetricName, FastCompass>();

        for (Entry<String, MetricRegistry> entry : metricRegistryMap.entrySet()) {

            MetricRegistry metricRegistry = entry.getValue();

            Map<MetricName, Metric> metrics = metricRegistry.getMetrics();

            for (Entry<MetricName, Metric> entry1 : metrics.entrySet()) {

                checkAndAdd(entry1, filter, gauges, counters, histograms, meters, timers, compasses, fastCompasses);

            }
        }

        result.put(Gauge.class, gauges);
        result.put(Counter.class, counters);
        result.put(Histogram.class, histograms);
        result.put(Meter.class, meters);
        result.put(Timer.class, timers);
        result.put(Compass.class, compasses);
        result.put(FastCompass.class, fastCompasses);

        return result;
    }

    private Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> getCategoryMetrics(
            Map<MetricName, Metric> metrics, MetricFilter filter) {
        if (!this.enabled) {
            return Collections.emptyMap();
        }

        Map<Class<? extends Metric>, Map<MetricName, ? extends Metric>> result = new HashMap<Class<? extends Metric>, Map<MetricName, ? extends Metric>>();

        Map<MetricName, Gauge> gauges = new HashMap<MetricName, Gauge>();
        Map<MetricName, Counter> counters = new HashMap<MetricName, Counter>();
        Map<MetricName, Histogram> histograms = new HashMap<MetricName, Histogram>();
        Map<MetricName, Meter> meters = new HashMap<MetricName, Meter>();
        Map<MetricName, Timer> timers = new HashMap<MetricName, Timer>();
        Map<MetricName, Compass> compasses = new HashMap<MetricName, Compass>();
        Map<MetricName, FastCompass> fastCompasses = new HashMap<MetricName, FastCompass>();

        for (Map.Entry<MetricName, Metric> entry : metrics.entrySet()) {
            checkAndAdd(entry, filter, gauges, counters, histograms, meters, timers, compasses, fastCompasses);
        }

        result.put(Gauge.class, gauges);
        result.put(Counter.class, counters);
        result.put(Histogram.class, histograms);
        result.put(Meter.class, meters);
        result.put(Timer.class, timers);
        result.put(Compass.class, compasses);
        result.put(FastCompass.class, fastCompasses);

        return result;
    }

    private void checkAndAdd(Map.Entry<MetricName, Metric> entry, MetricFilter filter, Map<MetricName, Gauge> gauges,
            Map<MetricName, Counter> counters, Map<MetricName, Histogram> histograms, Map<MetricName, Meter> meters,
            Map<MetricName, Timer> timers, Map<MetricName, Compass> compasses, Map<MetricName, FastCompass> fastCompasses) {

        MetricName metricName = entry.getKey();
        Metric metric = entry.getValue();
        if (metric instanceof Gauge && filter.matches(metricName, metric)) {
            gauges.put(metricName, (Gauge) metric);
        } else if (metric instanceof Counter && filter.matches(metricName, metric)) {
            counters.put(metricName, (Counter) metric);
        } else if (metric instanceof Histogram && filter.matches(metricName, metric)) {
            histograms.put(metricName, (Histogram) metric);
        } else if (metric instanceof Meter && filter.matches(metricName, metric)) {
            meters.put(metricName, (Meter) metric);
        } else if (metric instanceof Timer && filter.matches(metricName, metric)) {
            timers.put(metricName, (Timer) metric);
        } else if (metric instanceof Compass && filter.matches(metricName, metric)) {
            compasses.put(metricName, (Compass) metric);
        } else if (metric instanceof FastCompass && filter.matches(metricName, metric)) {
            fastCompasses.put(metricName, (FastCompass) metric);
        } else if (metric instanceof DynamicMetricSet) {
            DynamicMetricSet dynamicMetricSet = (DynamicMetricSet) metric;
            Map<MetricName, Metric> dynamicMetrics = dynamicMetricSet.getDynamicMetrics();
            for (Map.Entry<MetricName, Metric> dynamicMetricEntry : dynamicMetrics.entrySet()) {
                checkAndAdd(dynamicMetricEntry, filter, gauges, counters, histograms, meters, timers, compasses, fastCompasses);
            }
        }
    }

    @Override
    public void clear() {
        metricRegistryMap.clear();
    }

}
