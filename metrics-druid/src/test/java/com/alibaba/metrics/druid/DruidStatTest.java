package com.alibaba.metrics.druid;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DruidStatTest {

    private DruidDataSource dataSource;

    @Before
    public void setUp() throws Exception {
        TabularData sqlList = JdbcStatManager.getInstance().getSqlList();
        if (sqlList.size() > 0) {
            for (Object item : JdbcStatManager.getInstance().getSqlList().values()) {
                String text = JSONUtils.toJSONString(item);
                System.out.println(text);
            }
        }

        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setFilters("mergeStat");
        dataSource.setDbType("mysql");
    }

    @After
    public void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
    public void testJdbcConnection() throws Exception {
        for (int i = 1000; i < 2000; ++i) {
            String tableName = "t" + i;
            String sql = "select * from " + tableName + " where " + tableName + ".id = " + i;
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        }

        for (int i = 2000; i < 3000; ++i) {
            String tableName = "t" + i;
            String sql = "insert into " + tableName + " values()";
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();
            conn.close();
        }

        TabularData tabularData = JdbcStatManager.getInstance().getSqlList();

        List<Object> list = new ArrayList<Object>();
        TabularDataSupport tabularDataSupport = (TabularDataSupport)tabularData;
        for (Object itemValue : tabularDataSupport.values()) {
            if (itemValue instanceof CompositeData) {
                CompositeData compositeData = (CompositeData)itemValue;
                Map<String, Object> singleLine = new HashMap<String, Object>();
                for (String key : compositeData.getCompositeType().keySet()) {
                    Object entryValue = compositeData.get(key);
                    singleLine.put(key, entryValue);
                }
                list.add(singleLine);
            }
        }

        Assert.assertEquals(2, dataSource.getDataSourceStat().getSqlStatMap().size());
    }
}
