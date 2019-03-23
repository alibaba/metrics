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
package com.alibaba.metrics;

import com.alibaba.metrics.common.config.MetricsCollectPeriodConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A registry of metric instances.
 */
public class MetricRegistryImpl extends MetricRegistry {

    private static final int DEFAULT_MAX_METRIC_COUNT =
            Integer.getInteger("com.alibaba.metrics.maxMetricCountPerRegistry", 5000);

    // 用于分桶计数统计间隔配置
    private static final MetricsCollectPeriodConfig config = new MetricsCollectPeriodConfig();

    private final ConcurrentMap<MetricName, Metric> metrics;
    private final List<MetricRegistryListener> listeners;
    private final int maxMetricCount;

    /**
     * Creates a new {@link MetricRegistry}.
     */
    public MetricRegistryImpl() {
        this(DEFAULT_MAX_METRIC_COUNT);
    }

    public MetricRegistryImpl(int maxMetricCount) {
        this.metrics = new ConcurrentHashMap<MetricName, Metric>();
        this.listeners = new CopyOnWriteArrayList<MetricRegistryListener>();
        this.maxMetricCount = maxMetricCount;
    }

    /**
     * @see #register(MetricName, Metric)
     */
    public <T extends Metric> T register(String name, T metric) throws IllegalArgumentException {
        return register(MetricName.build(name), metric);
    }

    /**
     * Given a {@link Metric}, registers it under the given name.
     *
     * @param name   the name of the metric
     * @param metric the metric
     * @param <T>    the type of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered
     */
    public <T extends Metric> T register(MetricName name, T metric) throws IllegalArgumentException {
        if (metric instanceof MetricSet && !(metric instanceof DynamicMetricSet)) {
            registerAll(name, (MetricSet) metric);
        } else {
            final Metric existing = metrics.putIfAbsent(name, metric);
            if (existing == null) {
                onMetricAdded(name, metric);
            } else {
                throw new IllegalArgumentException("A metric named " + name + " already exists");
            }
        }
        return metric;
    }

    /**
     * Given a metric set, registers them.
     *
     * @param metrics    a set of metrics
     * @throws IllegalArgumentException if any of the names are already registered
     */
    public void registerAll(MetricSet metrics) throws IllegalArgumentException {
        registerAll(null, metrics);
    }

    /**
     * @see #counter(MetricName)
     */
    public Counter counter(String name) {
        return counter(MetricName.build(name));
    }

    /**
     * Return the {@link Counter} registered under this name; or create and register
     * a new {@link Counter} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Counter}
     */
    public Counter counter(MetricName name) {
        Counter counter = getOrAdd(name, COUNTER_BUILDER);
        if (counter == null) {
            return NOPMetricManager.NOP_COUNTER;
        }
        return counter;
    }

    /**
     * @see #histogram(MetricName)
     */
    public Histogram histogram(String name) {
        return histogram(MetricName.build(name));
    }

    /**
     * Return the {@link Histogram} registered under this name; or create and register
     * a new {@link Histogram} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Histogram}
     */
    public Histogram histogram(MetricName name) {
        return histogram(name, ReservoirType.EXPONENTIALLY_DECAYING);
    }

    /**
     * Create a histogram with given name, and reservoir type
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a histogram instance
     */
    @Override
    public Histogram histogram(MetricName name, ReservoirType type) {
        Histogram histogram = getOrAdd(name, HISTOGRAM_BUILDER, type);
        if (histogram == null) {
            return NOPMetricManager.NOP_HISTOGRAM;
        }
        return histogram;
    }

    /**
     * @see #meter(MetricName)
     */
    public Meter meter(String name) {
        return meter(MetricName.build(name));
    }

    /**
     * Return the {@link Meter} registered under this name; or create and register
     * a new {@link Meter} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Meter}
     */
    public Meter meter(MetricName name) {
        Meter meter = getOrAdd(name, METER_BUILDER);
        if (meter == null) {
            return NOPMetricManager.NOP_METER;
        }
        return meter;
    }

    /**
     * @see #timer(MetricName)
     */
    public Timer timer(String name) {
        return timer(MetricName.build(name));
    }

    /**
     * Return the {@link Timer} registered under this name; or create and register
     * a new {@link Timer} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Timer}
     */
    public Timer timer(MetricName name) {
        return timer(name, ReservoirType.EXPONENTIALLY_DECAYING);
    }

    /**
     * Create a timer with given name, and reservoir type
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a timer instance
     */
    @Override
    public Timer timer(MetricName name, ReservoirType type) {
        Timer timer = getOrAdd(name, TIMER_BUILDER, type);
        if (timer == null) {
            return NOPMetricManager.NOP_TIMER;
        }
        return timer;
    }

    /**
     * Return the {@link Compass} registered under this name; or create and register
     * a new {@link Timer} if none is registered.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Compass}
     */
    @Override
    public Compass compass(MetricName name) {
        return compass(name, ReservoirType.EXPONENTIALLY_DECAYING);
    }

    /**
     * Create a compass with given name, and reservoir type
     * @param name the name of the metric
     * @param type the type of reservoir
     * @return a compass instance
     */
    @Override
    public Compass compass(MetricName name, ReservoirType type) {
        Compass compass = getOrAdd(name, COMPASS_BUILDER, type);
        if (compass == null) {
            return NOPMetricManager.NOP_COMPASS;
        }
        return compass;
    }

    /**
     * @see #compass(MetricName)
     */
    @Override
    public Compass compass(String name) {
        return compass(MetricName.build(name));
    }

    @Override
    public FastCompass fastCompass(MetricName name) {
        FastCompass compass = getOrAdd(name, FAST_COMPASS_BUILDER);
        if (compass == null) {
            return NOPMetricManager.NOP_FAST_COMPASS;
        }
        return compass;
    }

    @Override
    public ClusterHistogram clusterHistogram(MetricName name, long[] buckets) {
        ClusterHistogram clusterHistogram = getOrAddClusterHistogram(name, CLUSTER_HISTOGRAM_BUILDER, buckets);
        if (clusterHistogram == null) {
            return NOPMetricManager.NOP_CLUSTER_HISTOGRAM;
        }
        return clusterHistogram;
    }

    /**
     * Removes the metric with the given name.
     *
     * @param name the name of the metric
     * @return whether or not the metric was removed
     */
    public boolean remove(MetricName name) {
        final Metric metric = metrics.remove(name);
        if (metric != null) {
            onMetricRemoved(name, metric);
            return true;
        }
        return false;
    }

    /**
     * Removes all metrics which match the given filter.
     *
     * @param filter a filter
     */
    public void removeMatching(MetricFilter filter) {
        for (Map.Entry<MetricName, Metric> entry : metrics.entrySet()) {
            if (filter.matches(entry.getKey(), entry.getValue())) {
                remove(entry.getKey());
            }
        }
    }

    /**
     * Adds a {@link MetricRegistryListener} to a collection of listeners that will be notified on
     * metric creation.  Listeners will be notified in the order in which they are added.
     * <p/>
     * <b>N.B.:</b> The listener will be notified of all existing metrics when it first registers.
     *
     * @param listener the listener that will be notified
     */
    public void addListener(MetricRegistryListener listener) {
        listeners.add(listener);

        for (Map.Entry<MetricName, Metric> entry : metrics.entrySet()) {
            notifyListenerOfAddedMetric(listener, entry.getValue(), entry.getKey());
        }
    }

    /**
     * Removes a {@link MetricRegistryListener} from this registry's collection of listeners.
     *
     * @param listener the listener that will be removed
     */
    public void removeListener(MetricRegistryListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns a set of the names of all the metrics in the registry.
     *
     * @return the names of all the metrics
     */
    public SortedSet<MetricName> getNames() {
        return Collections.unmodifiableSortedSet(new TreeSet<MetricName>(metrics.keySet()));
    }

    /**
     * Returns a map of all the gauges in the registry and their names.
     *
     * @return all the gauges in the registry
     */
    public SortedMap<MetricName, Gauge> getGauges() {
        return getGauges(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the gauges in the registry and their names which match the given filter.
     *
     * @param filter    the metric filter to match
     * @return all the gauges in the registry
     */
    public SortedMap<MetricName, Gauge> getGauges(MetricFilter filter) {
        return getMetrics(Gauge.class, filter);
    }

    /**
     * Returns a map of all the counters in the registry and their names.
     *
     * @return all the counters in the registry
     */
    public SortedMap<MetricName, Counter> getCounters() {
        return getCounters(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the counters in the registry and their names which match the given
     * filter.
     *
     * @param filter    the metric filter to match
     * @return all the counters in the registry
     */
    public SortedMap<MetricName, Counter> getCounters(MetricFilter filter) {
        return getMetrics(Counter.class, filter);
    }

    /**
     * Returns a map of all the histograms in the registry and their names.
     *
     * @return all the histograms in the registry
     */
    public SortedMap<MetricName, Histogram> getHistograms() {
        return getHistograms(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the histograms in the registry and their names which match the given
     * filter.
     *
     * @param filter    the metric filter to match
     * @return all the histograms in the registry
     */
    public SortedMap<MetricName, Histogram> getHistograms(MetricFilter filter) {
        return getMetrics(Histogram.class, filter);
    }

    /**
     * Returns a map of all the meters in the registry and their names.
     *
     * @return all the meters in the registry
     */
    public SortedMap<MetricName, Meter> getMeters() {
        return getMeters(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the meters in the registry and their names which match the given filter.
     *
     * @param filter    the metric filter to match
     * @return all the meters in the registry
     */
    public SortedMap<MetricName, Meter> getMeters(MetricFilter filter) {
        return getMetrics(Meter.class, filter);
    }

    /**
     * Returns a map of all the timers in the registry and their names.
     *
     * @return all the timers in the registry
     */
    public SortedMap<MetricName, Timer> getTimers() {
        return getTimers(MetricFilter.ALL);
    }

    /**
     * Returns a map of all the timers in the registry and their names which match the given filter.
     *
     * @param filter    the metric filter to match
     * @return all the timers in the registry
     */
    public SortedMap<MetricName, Timer> getTimers(MetricFilter filter) {
        return getMetrics(Timer.class, filter);
    }

    @Override
    public SortedMap<MetricName, Compass> getCompasses(MetricFilter filter) {
        return getMetrics(Compass.class, filter);
    }

    @Override
    public SortedMap<MetricName, Compass> getCompasses() {
        return getCompasses(MetricFilter.ALL);
    }

    @Override
    public SortedMap<MetricName, FastCompass> getFastCompasses() {
        return getFastCompasses(MetricFilter.ALL);
    }

    @Override
    public SortedMap<MetricName, FastCompass> getFastCompasses(MetricFilter filter) {
        return getMetrics(FastCompass.class, filter);
    }


    @Override
    public SortedMap<MetricName, ClusterHistogram> getClusterHistograms(MetricFilter filter) {
        return getMetrics(ClusterHistogram.class, filter);
    }

    @Override
    public SortedMap<MetricName, Metric> getMetrics(MetricFilter filter) {
        final TreeMap<MetricName, Metric> filteredMetrics = new TreeMap<MetricName, Metric>();
        filteredMetrics.putAll(getCounters(filter));
        filteredMetrics.putAll(getMeters(filter));
        filteredMetrics.putAll(getHistograms(filter));
        filteredMetrics.putAll(getGauges(filter));
        filteredMetrics.putAll(getTimers(filter));
        filteredMetrics.putAll(getCompasses(filter));
        filteredMetrics.putAll(getFastCompasses(filter));
        return Collections.unmodifiableSortedMap(filteredMetrics);
    }

    /**
     * This is an expensive method that will traverse all the metrics, it should be used under a low frequency.
     * @return the last updated time for the entire MetricRegistry
     */
    @Override
    public long lastUpdateTime() {
        long latest = 0;
        Map<MetricName, Metric> metrics = new HashMap<MetricName, Metric>();
        metrics.putAll(getCounters(MetricFilter.ALL));
        metrics.putAll(getMeters(MetricFilter.ALL));
        metrics.putAll(getHistograms(MetricFilter.ALL));
        metrics.putAll(getGauges(MetricFilter.ALL));
        metrics.putAll(getTimers(MetricFilter.ALL));
        metrics.putAll(getCompasses(MetricFilter.ALL));
        metrics.putAll(getFastCompasses(MetricFilter.ALL));
        for (Map.Entry<MetricName, Metric> entry: metrics.entrySet()) {
            if (latest < entry.getValue().lastUpdateTime()) {
                latest = entry.getValue().lastUpdateTime();
            }
        }
        return latest;
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> T getOrAdd(MetricName name, MetricBuilder<T> builder) {
        final Metric metric = metrics.get(name);
        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                T newMetric = builder.newMetric(name);
                if (newMetric == null) return null;
                return register(name, newMetric);
            } catch (IllegalArgumentException e) {
                final Metric added = metrics.get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                } else {
                    throw e;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> T getOrAdd(MetricName name, ReservoirTypeBuilder<T> builder, ReservoirType type) {
        final Metric metric = metrics.get(name);
        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                T newMetric = builder.newMetric(name, type);
                if (newMetric == null) return null;
                return register(name, newMetric);
            } catch (IllegalArgumentException e) {
                final Metric added = metrics.get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                } else {
                    throw e;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> T getOrAddClusterHistogram(MetricName name, ClusterHistogramBuilder<T> builder, long[] buckets) {
        final Metric metric = metrics.get(name);
        if (builder.isInstance(metric)) {
            return (T) metric;
        } else if (metric == null) {
            try {
                T newMetric = builder.newMetric(name, buckets);
                if (newMetric == null) return null;
                return register(name, newMetric);
            } catch (IllegalArgumentException e) {
                final Metric added = metrics.get(name);
                if (builder.isInstance(added)) {
                    return (T) added;
                } else {
                    throw e;
                }
            }
        }
        throw new IllegalArgumentException(name + " is already used for a different type of metric");
    }

    @SuppressWarnings("unchecked")
    private <T extends Metric> SortedMap<MetricName, T> getMetrics(Class<T> klass, MetricFilter filter) {
        final TreeMap<MetricName, T> timers = new TreeMap<MetricName, T>();
        for (Map.Entry<MetricName, Metric> entry : metrics.entrySet()) {
            if (klass.isInstance(entry.getValue()) && filter.matches(entry.getKey(),
                    entry.getValue())) {
                timers.put(entry.getKey(), (T) entry.getValue());
            } else if (entry.getValue() instanceof DynamicMetricSet) {
                for (Map.Entry<MetricName, Metric> dynamicEntry:
                        ((DynamicMetricSet) entry.getValue()).getDynamicMetrics().entrySet()) {
                    if (klass.isInstance(dynamicEntry.getValue()) &&
                            filter.matches(dynamicEntry.getKey(), dynamicEntry.getValue())) {
                        timers.put(dynamicEntry.getKey(), (T) dynamicEntry.getValue());
                    }
                }
            }
        }
        return Collections.unmodifiableSortedMap(timers);
    }

    private void onMetricAdded(MetricName name, Metric metric) {
        for (MetricRegistryListener listener : listeners) {
            notifyListenerOfAddedMetric(listener, metric, name);
        }
    }

    private void notifyListenerOfAddedMetric(MetricRegistryListener listener, Metric metric, MetricName name) {
        if (metric instanceof Gauge) {
            listener.onGaugeAdded(name, (Gauge<?>) metric);
        } else if (metric instanceof Counter) {
            listener.onCounterAdded(name, (Counter) metric);
        } else if (metric instanceof Histogram) {
            listener.onHistogramAdded(name, (Histogram) metric);
        } else if (metric instanceof Meter) {
            listener.onMeterAdded(name, (Meter) metric);
        } else if (metric instanceof Timer) {
            listener.onTimerAdded(name, (Timer) metric);
        } else if (metric instanceof Compass) {
            listener.onCompassAdded(name, (Compass) metric);
        } else if (metric instanceof FastCompass) {
            listener.onFastCompassAdded(name, (FastCompass) metric);
        } else {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
        }
    }

    private void onMetricRemoved(MetricName name, Metric metric) {
        for (MetricRegistryListener listener : listeners) {
            notifyListenerOfRemovedMetric(name, metric, listener);
        }
    }

    private void notifyListenerOfRemovedMetric(MetricName name, Metric metric, MetricRegistryListener listener) {
        if (metric instanceof Gauge) {
            listener.onGaugeRemoved(name);
        } else if (metric instanceof Counter) {
            listener.onCounterRemoved(name);
        } else if (metric instanceof Histogram) {
            listener.onHistogramRemoved(name);
        } else if (metric instanceof Meter) {
            listener.onMeterRemoved(name);
        } else if (metric instanceof Timer) {
            listener.onTimerRemoved(name);
        } else if (metric instanceof Compass) {
            listener.onCompassRemoved(name);
        } else if (metric instanceof FastCompass) {
            listener.onFastCompassRemoved(name);
        } else {
            throw new IllegalArgumentException("Unknown metric type: " + metric.getClass());
        }
    }

    private void registerAll(MetricName prefix, MetricSet metrics) throws IllegalArgumentException {
        if (prefix == null)
            prefix = MetricName.EMPTY;

        for (Map.Entry<MetricName, Metric> entry : metrics.getMetrics().entrySet()) {
            if (entry.getValue() instanceof MetricSet && !(entry.getValue() instanceof DynamicMetricSet)) {
                // skip dynamic metric set, the metrics will be collected later on.
                registerAll(MetricName.join(prefix, entry.getKey()), (MetricSet) entry.getValue());
            } else {
                register(MetricName.join(prefix, entry.getKey()), entry.getValue());
            }
        }
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return Collections.unmodifiableMap(metrics);
    }

    /**
     * A quick and easy way of capturing the notion of default metrics.
     */
    private MetricBuilder<Counter> COUNTER_BUILDER = new MetricBuilder<Counter>() {
        @Override
        public Counter newMetric(MetricName name) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new BucketCounterImpl(config.period(name.getMetricLevel()));
        }

        @Override
        public boolean isInstance(Metric metric) {
            return Counter.class.isInstance(metric);
        }
    };

    private ReservoirTypeBuilder<Histogram> HISTOGRAM_BUILDER = new ReservoirTypeBuilder<Histogram>() {
        @Override
        public Histogram newMetric(MetricName name, ReservoirType type) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new HistogramImpl(config.period(name.getMetricLevel()), type);
        }

        @Override
        public Histogram newMetric(MetricName name) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new HistogramImpl(config.period(name.getMetricLevel()));
        }

        @Override
        public boolean isInstance(Metric metric) {
            return Histogram.class.isInstance(metric);
        }
    };

    private MetricBuilder<Meter> METER_BUILDER = new MetricBuilder<Meter>() {
        @Override
        public Meter newMetric(MetricName name) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new MeterImpl(config.period(name.getMetricLevel()));
        }

        @Override
        public boolean isInstance(Metric metric) {
            return Meter.class.isInstance(metric);
        }
    };

    private ReservoirTypeBuilder<Timer> TIMER_BUILDER = new ReservoirTypeBuilder<Timer>() {
        @Override
        public Timer newMetric(MetricName name) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new TimerImpl(config.period(name.getMetricLevel()));
        }

        @Override
        public Timer newMetric(MetricName name, ReservoirType type) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new TimerImpl(config.period(name.getMetricLevel()), type);
        }

        @Override
        public boolean isInstance(Metric metric) {
            return Timer.class.isInstance(metric);
        }
    };

    private ReservoirTypeBuilder<Compass> COMPASS_BUILDER = new ReservoirTypeBuilder<Compass>() {
        @Override
        public Compass newMetric(MetricName name) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new CompassImpl(config.period(name.getMetricLevel()));
        }

        @Override
        public Compass newMetric(MetricName name, ReservoirType type) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new CompassImpl(config.period(name.getMetricLevel()), type);
        }

        @Override
        public boolean isInstance(Metric metric) {
            return Compass.class.isInstance(metric);
        }
    };

    private MetricBuilder<FastCompass> FAST_COMPASS_BUILDER = new MetricBuilder<FastCompass>() {
        @Override
        public FastCompass newMetric(MetricName name) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new FastCompassImpl(config.period(name.getMetricLevel()));
        }

        @Override
        public boolean isInstance(Metric metric) {
            return FastCompass.class.isInstance(metric);
        }
    };

    private ClusterHistogramBuilder<ClusterHistogram> CLUSTER_HISTOGRAM_BUILDER = new ClusterHistogramBuilder<ClusterHistogram>() {
        @Override
        public ClusterHistogram newMetric(MetricName name, long[] buckets) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new ClusterHistogramImpl(buckets, config.period(name.getMetricLevel()), Clock.defaultClock());
        }

        @Override
        public ClusterHistogram newMetric(MetricName name) {
            // 当已注册的metric数量太多时，返回一个空实现
            if (metrics.size() >= maxMetricCount) {
                return null;
            }
            return new ClusterHistogramImpl(config.period(name.getMetricLevel()), Clock.defaultClock());
        }

        @Override
        public boolean isInstance(Metric metric) {
            return metric instanceof ClusterHistogram;
        }
    };

}
