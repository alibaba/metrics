package com.alibaba.metrics.os.linux;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import com.alibaba.metrics.RatioGauge;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Collect disk related statistics
 */
public class DiskStatGaugeSet extends CachedMetricSet {

    private Map<MetricName, Metric> gauges;

    /**
     * The partitions of the disk, each is represented by a File object
     */
    private File[] partitions;

    /**
     * the total partition space in bytes
     */
    private long[] partitionTotalSpace;

    /**
     * the free partition space in bytes
     */
    private long[] partitionFreeSpace;

    public DiskStatGaugeSet() {
        this(DEFAULT_DATA_TTL, TimeUnit.MILLISECONDS, Clock.defaultClock(), new File[]{ new File("/") });
    }

    public DiskStatGaugeSet(long dataTTL, TimeUnit unit) {
        this(dataTTL, unit, Clock.defaultClock(), new File[]{ new File("/") });
    }

    public DiskStatGaugeSet(long dataTTL, TimeUnit unit, Clock clock, File[] partitions) {
        super(dataTTL, unit, clock);
        this.gauges = new HashMap<MetricName, Metric>();
        // keep the partitions file array immutable
        if (partitions == null) {
            this.partitions = new File[0];
            this.partitionTotalSpace = new long[0];
            this.partitionFreeSpace = new long[0];
        } else {
            this.partitions = new File[partitions.length];
            for (int i = 0; i < partitions.length; i++) {
                this.partitions[i] = partitions[i];
            }
            this.partitionTotalSpace = new long[partitions.length];
            this.partitionFreeSpace = new long[partitions.length];
        }
        populateGauges();
    }

    /**
     * According to the documentation, getUsableSpace() is more accurate than getFreeSpace()
     */
    @Override
    protected void getValueInternal() {
        for (int i = 0; i < partitions.length; i++) {
            partitionTotalSpace[i] = partitions[i].getTotalSpace();
            partitionFreeSpace[i] = partitions[i].getUsableSpace();
        }
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return gauges;
    }

    private void populateGauges() {
        for (int i = 0; i < partitions.length; i++) {
            String path = partitions[i].getAbsolutePath();
            gauges.put(MetricName.build("disk.partition.total").tagged("partition", path),
                    new PartitionTotalGauge(i));
            gauges.put(MetricName.build("disk.partition.free").tagged("partition", path),
                    new PartitionFreeGauge(i));
            gauges.put(MetricName.build("disk.partition.used_ratio").tagged("partition", path),
                    new PartitionUsageGauge(i));
        }
    }

    private class PartitionTotalGauge extends PersistentGauge<Long> {

        private int index;

        public PartitionTotalGauge(int index) {
            this.index = index;
        }

        @Override
        public Long getValue() {
            try {
                refreshIfNecessary();
                return partitionTotalSpace[index];
            } catch (Exception e) {
                return 0L;
            }
        }
    }

    private class PartitionFreeGauge extends PersistentGauge<Long> {

        private int index;

        public PartitionFreeGauge(int index) {
            this.index = index;
        }

        @Override
        public Long getValue() {
            try {
                refreshIfNecessary();
                return partitionFreeSpace[index];
            } catch (Exception e) {
                return 0L;
            }
        }
    }

    private class PartitionUsageGauge extends RatioGauge {

        private int index;

        public PartitionUsageGauge(int index) {
            this.index = index;
        }

        @Override
        protected Ratio getRatio() {
            return Ratio.of((double)(partitionTotalSpace[index] - partitionFreeSpace[index]),
                    (double)partitionTotalSpace[index]);
        }
    }
}
