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
package com.alibaba.metrics.os.windows;

import com.alibaba.metrics.CachedMetricSet;
import com.alibaba.metrics.Clock;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.PersistentGauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class NetTrafficGaugeSet extends CachedMetricSet {

    private static final Logger logger = LoggerFactory.getLogger(NetTrafficGaugeSet.class);

    private static final String[] FIELDS = {
            "net.in.bytes", "net.in.packets", "net.in.errs", "net.in.dropped",
            "net.in.fifo.errs", "net.in.frame.errs", "net.in.compressed", "net.in.multicast",
            "net.out.bytes", "net.out.packets", "net.out.errs", "net.out.dropped",
            "net.out.fifo.errs", "net.out.collisions", "net.out.carrier.errs", "net.out.compressed"
    };

    private Map<String, Long[]> countByFace;
    private Map<String, Double[]> rateByFace;

    private Map<MetricName, Metric> gauges;

    public NetTrafficGaugeSet(long dataTTL, TimeUnit unit) {
        this(dataTTL, unit, Clock.defaultClock());
    }

    public NetTrafficGaugeSet(long dataTTL, TimeUnit unit, Clock clock) {
        super(dataTTL, unit, clock);
        this.gauges = new HashMap<MetricName, Metric>();
        countByFace = new HashMap<String, Long[]>();
        rateByFace = new HashMap<String, Double[]>();
        getValueInternal();
        populateGauges();
    }

    @Override
    public Map<MetricName, Metric> getMetrics() {
        return null;
    }

    @Override
    protected void getValueInternal() {

//        String[] lst = sigar.getNetInterfaceList();
//        NetInterfaceStat netStat;
//
//        if (lst != null && lst.length > 2){
//            netStat = sigar.getNetInterfaceStat(lst[1]);
//        }
//
//        try {
//
//            for (String line: lines) {
//                Matcher netMatcher = NET_PATTERN.matcher(line);
//
//                if (netMatcher.matches()) {
//                    String face = netMatcher.group(1);
//                    if (countByFace.get(face) == null) {
//                        Long[] counts = new Long[FIELDS.length];
//                        Double[] rates = new Double[FIELDS.length];
//                        for (int i=0; i<counts.length; i++) {
//                            counts[i] = 0L;
//                            rates[i] = 0.0d;
//                        }
//                        countByFace.put(face, counts);
//                        rateByFace.put(face, rates);
//                    }
//
//                    String[] stats = netMatcher.group(2).trim().split(DELIM);
//                    for (int i = 0; i < stats.length; i++) {
//                        try {
//                            long count = Long.parseLong(stats[i]);
//                            long delta = count - countByFace.get(face)[i];
//                            countByFace.get(face)[i] = count;
//                            long duration = clock.getTime() - lastCollectTime.get();
//                            rateByFace.get(face)[i] = 1000.0d * delta / duration;
//                        } catch (Exception e) {
//                            logger.warn("Error parsing net traffic metrics:", e);
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            logger.warn("Error during reading file {}", filePath, e);
//        }
    }

    private class NetGauge extends PersistentGauge<Double> {

        private String face;
        private int index;

        public NetGauge(String face, int index) {
            this.face = face;
            this.index = index;
        }

        @Override
        public Double getValue() {
            try {
                refreshIfNecessary();
                return rateByFace.get(face)[index];
            } catch (Exception e) {
                return 0.0d;
            }
        }
    }

    private void populateGauges() {
        for (Map.Entry<String, Long[]> entry: countByFace.entrySet()) {
            for (int i = 0; i < entry.getValue().length; i++) {
                gauges.put(MetricName.build(FIELDS[i]).tagged("face", entry.getKey()),
                        new NetGauge(entry.getKey(), i));
            }
        }
    }

}
