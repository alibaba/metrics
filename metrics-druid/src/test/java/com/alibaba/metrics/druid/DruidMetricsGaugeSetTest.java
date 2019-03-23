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
package com.alibaba.metrics.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

public class DruidMetricsGaugeSetTest {

    @After
    public void tearDown() {
        JdbcStatManager.getInstance().reset();
    }

    @Test
    public void testDruidMetricsSet() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setFilters("mergeStat");
        dataSource.setDbType("mysql");

        ManualClock clock = new ManualClock();
        MetricName name = new MetricName("druid2.sql");
        MetricManager.register("druid2", name,
                new DruidMetricsGaugeSet(5, TimeUnit.SECONDS, clock,20, name));

        doSelect(dataSource, 1000, 2000);
        doUpdate(dataSource, 2000, 2500);
        clock.addSeconds(6);

        SortedMap<MetricName, Gauge> druidMetrics = MetricManager.getIMetricManager().getGauges("druid2", MetricFilter.ALL);
        Assert.assertEquals(26, druidMetrics.size());
        Gauge g = druidMetrics.get(new MetricName("druid2.sql.ExecuteCount")
                .tagged("sql", "SELECT *\nFROM t\nWHERE t.id = ?"));
        Assert.assertEquals(1000L, g.getValue());

        doSelect(dataSource, 2000, 2500);
        clock.addSeconds(6);

        druidMetrics = MetricManager.getIMetricManager().getGauges("druid2", MetricFilter.ALL);
        Assert.assertEquals(26, druidMetrics.size());
        Gauge g2 = druidMetrics.get(new MetricName("druid2.sql.ExecuteCount")
                .tagged("sql", "SELECT *\nFROM t\nWHERE t.id = ?"));
        Assert.assertEquals(500L, g2.getValue());
    }


    @Test
    public void testMaxSqlSize() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setFilters("mergeStat");
        dataSource.setDbType("mysql");

        ManualClock clock = new ManualClock();
        MetricName name = new MetricName("druid.sql");
        MetricManager.register("druid", name,
                new DruidMetricsGaugeSet(5, TimeUnit.SECONDS, clock,1,name));

        doSelect(dataSource, 1000, 2000);
        doUpdate(dataSource, 2000, 2500);
        clock.addSeconds(6);

        SortedMap<MetricName, Gauge> druidMetrics = MetricManager.getIMetricManager().getGauges("druid", MetricFilter.ALL);
        Assert.assertEquals(13, druidMetrics.size());
        // on Java 6 TabularDataSupport returns unordered list (HashMap)
        // So the return value it uncertain, it may be select or update
        // on Java 8+  TabularDataSupport returns ordered list (LinkedHashMap)
        Gauge g1 = druidMetrics.get(new MetricName("druid.sql.ExecuteCount")
                .tagged("sql", "SELECT *\nFROM t\nWHERE t.id = ?"));
        Gauge g2 = druidMetrics.get(new MetricName("druid.sql.ExecuteCount")
                .tagged("sql", "UPDATE t\nSET name = ?\nWHERE t.id = ?"));
        if (null != g1) {
            Assert.assertEquals(1000L, g1.getValue());
        } else {
            Assert.assertEquals(500L, g2.getValue());
        }
    }


    private void doSelect(DruidDataSource dataSource, int start, int end) throws Exception {
        for (int i = start; i < end; ++i) {
            String tableName = "t" + i;
            String sql = "select * from " + tableName + " where " + tableName + ".id = " + i;
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        }
    }

    private void doUpdate(DruidDataSource dataSource, int start, int end) throws Exception {
        for (int i = start; i < end; ++i) {
            String tableName = "t" + i;
            String sql = "update " + tableName + " set name='aaa' where " + tableName + ".id = " + i;
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        }
    }
}
