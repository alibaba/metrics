package com.alibaba.metrics.jvm;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.MetricSet;
import com.alibaba.metrics.PersistentGauge;
import com.alibaba.metrics.RatioGauge;

/**
 * A set of gauges for JVM memory usage, including stats on heap vs. non-heap memory, plus
 * GC-specific memory pools.
 * +----------------------------------------------+
 * +////////////////           |                  +
 * +////////////////           |                  +
 * +----------------------------------------------+
 *
 * |--------|
 *   init
 * |---------------|
 *        used
 * |---------------------------|
 *             committed
 * |----------------------------------------------|
 *                         max
 */
public class MemoryUsageGaugeSet implements MetricSet {
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");

    private final MemoryMXBean mxBean;
    private final List<MemoryPoolMXBean> memoryPools;

    public MemoryUsageGaugeSet() {
        this(ManagementFactory.getMemoryMXBean(),
             ManagementFactory.getMemoryPoolMXBeans());
    }

    public MemoryUsageGaugeSet(MemoryMXBean mxBean,
                               Collection<MemoryPoolMXBean> memoryPools) {
        this.mxBean = mxBean;
        this.memoryPools = new ArrayList<MemoryPoolMXBean>(memoryPools);
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        final Map<MetricName, Metric> gauges = new HashMap<MetricName, Metric>();

        gauges.put(MetricName.build("total.init"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getHeapMemoryUsage().getInit() +
                        mxBean.getNonHeapMemoryUsage().getInit();
            }
        });

        gauges.put(MetricName.build("total.used"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getHeapMemoryUsage().getUsed() +
                        mxBean.getNonHeapMemoryUsage().getUsed();
            }
        });

        gauges.put(MetricName.build("total.max"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getHeapMemoryUsage().getMax() +
                        mxBean.getNonHeapMemoryUsage().getMax();
            }
        });

        gauges.put(MetricName.build("total.committed"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getHeapMemoryUsage().getCommitted() +
                        mxBean.getNonHeapMemoryUsage().getCommitted();
            }
        });


        gauges.put(MetricName.build("heap.init"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getHeapMemoryUsage().getInit();
            }
        });

        gauges.put(MetricName.build("heap.used"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getHeapMemoryUsage().getUsed();
            }
        });

        gauges.put(MetricName.build("heap.max"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getHeapMemoryUsage().getMax();
            }
        });

        gauges.put(MetricName.build("heap.committed"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getHeapMemoryUsage().getCommitted();
            }
        });

        gauges.put(MetricName.build("heap.usage"), new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                final MemoryUsage usage = mxBean.getHeapMemoryUsage();
                return Ratio.of(usage.getUsed(), usage.getMax() == -1 ? usage.getCommitted() : usage.getMax());
            }
        });

        gauges.put(MetricName.build("non_heap.init"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getNonHeapMemoryUsage().getInit();
            }
        });

        gauges.put(MetricName.build("non_heap.used"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getNonHeapMemoryUsage().getUsed();
            }
        });

        gauges.put(MetricName.build("non_heap.max"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getNonHeapMemoryUsage().getMax();
            }
        });

        gauges.put(MetricName.build("non_heap.committed"), new PersistentGauge<Long>() {
            @Override
            public Long getValue() {
                return mxBean.getNonHeapMemoryUsage().getCommitted();
            }
        });

        gauges.put(MetricName.build("non_heap.usage"), new RatioGauge() {
            @Override
            protected Ratio getRatio() {
                final MemoryUsage usage = mxBean.getNonHeapMemoryUsage();
                return Ratio.of(usage.getUsed(), usage.getMax() == -1 ? usage.getCommitted() : usage.getMax());
            }
        });

        for (final MemoryPoolMXBean pool : memoryPools) {
            final MetricName poolName = MetricRegistry.name("pools",
                    WHITESPACE.matcher(pool.getName()).replaceAll("_").toLowerCase());

            gauges.put(poolName.resolve("usage"),
                    new RatioGauge() {
                           @Override
                           protected Ratio getRatio() {
                               MemoryUsage usage = pool.getUsage();
                               return Ratio.of(usage.getUsed(),
                                       usage.getMax() == -1 ? usage.getCommitted() : usage.getMax());
                           }
                    });

            gauges.put(poolName.resolve("max"),new PersistentGauge<Long>() {
                @Override
                public Long getValue() {
                    return pool.getUsage().getMax();
                }
            });

            gauges.put(poolName.resolve("used"),new PersistentGauge<Long>() {
                @Override
                public Long getValue() {
                    return pool.getUsage().getUsed();
                }
            });

            gauges.put(poolName.resolve("committed"),new PersistentGauge<Long>() {
                @Override
                public Long getValue() {
                    return pool.getUsage().getCommitted();
                }
            });

            // Only register GC usage metrics if the memory pool supports usage statistics.
            if (pool.getCollectionUsage() != null) {
                gauges.put(poolName.resolve("used_after_gc"),new PersistentGauge<Long>() {
                    @Override
                    public Long getValue() {
                        return pool.getCollectionUsage().getUsed();
                    }
                });
            }

            gauges.put(poolName.resolve("init"),new PersistentGauge<Long>() {
                @Override
                public Long getValue() {
                    return pool.getUsage().getInit();
                }
            });
        }

        return Collections.unmodifiableMap(gauges);
    }

    @Override
    public long lastUpdateTime() {
        return System.currentTimeMillis();
    }
}
