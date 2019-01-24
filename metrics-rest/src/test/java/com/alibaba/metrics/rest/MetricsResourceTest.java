package com.alibaba.metrics.rest;

import com.alibaba.fastjson.support.jaxrs.FastJsonAutoDiscoverable;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.CompassImpl;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.ManualClock;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.ReservoirType;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import com.alibaba.metrics.common.NormalMetricsCollector;
import com.alibaba.metrics.common.filter.MetricNameSetFilter;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * The mocking logic is located in {@link com.alibaba.metrics.MetricManagerBinder}
 */
public class MetricsResourceTest extends JerseyTest {

    private static final int SLEEP_TIME = 15000;

    @Override
    protected void configureClient(ClientConfig config) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        config.register(new JacksonJsonProvider(mapper));
    }

    @Override
    protected Application configure() {
        FastJsonAutoDiscoverable.autoDiscover = false;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ResourceConfig config = new ResourceConfig(MetricsResource.class);
        config.register(new JacksonJsonProvider(mapper));
        return config;
    }

    @Test
    public void testListMetrics() {
        MetricManager.getIMetricManager().clear();
        MetricManager.getCounter("dubbo", MetricName.build("middleware.dubbo.provider"));
        MetricManager.getMeter("test", MetricName.build("shared.carts.my_cart").level(MetricLevel.CRITICAL)).mark();
        MetricManager.getMeter("test", MetricName.build("shared.carts.my_cart")
                .level(MetricLevel.CRITICAL).tagged("source", "taobao")).mark();

        GenericType<Map<String, Object>> genericType = new GenericType<Map<String, Object>>(){};
        Map<String, Object> result = target("metrics/list").request()
                .accept(MediaType.APPLICATION_JSON_TYPE).get(genericType);

        Assert.assertEquals("success should be true", true, result.get("success"));
        Assert.assertTrue("data should be a map", result.get("data") instanceof Map);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Set<MetricObject>> data =
                mapper.convertValue(result.get("data"), new TypeReference<Map<String, Set<MetricObject>>>(){});
        Assert.assertTrue(data.get("dubbo").contains(MetricObject.named("middleware.dubbo.provider.count")
                .withType(MetricObject.MetricType.COUNTER).withLevel(MetricLevel.NORMAL).build()));
        Assert.assertEquals(".count/.m1/.m5/.m15/.bucket_count/qps * 2 = 12", 12, data.get("test").size());
        Assert.assertTrue("shared.carts.my_cart.m1 should be CRITICAL level",
                data.get("test").contains(MetricObject.named("shared.carts.my_cart.m1")
                .withLevel(MetricLevel.CRITICAL).withType(MetricObject.MetricType.GAUGE).build()));
        Map<String, String> tags = new HashMap<String, String>();
        tags.put("source", "taobao");
        Assert.assertTrue(data.get("test").contains(MetricObject.named("shared.carts.my_cart.m1").withTags(tags)
                .withLevel(MetricLevel.CRITICAL).withType(MetricObject.MetricType.GAUGE).build()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetMetrics() {
        Counter c1 = MetricManager.getCounter("dubbo111", MetricName.build("middleware.dubbo.provider"));
        c1.inc();
        Meter m1 = MetricManager.getMeter("carts111", MetricName.build("shared.carts.my_cart"));
        m1.mark();
        Meter m2 = MetricManager.getMeter("carts111", MetricName.build("shared.carts.my_cart").tagged("source", "taobao"));
        m2.mark(10);

        Response dubboResponse = target("metrics/dubbo111").request().accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        Map<String, Object> res = dubboResponse.readEntity(Map.class);

        Assert.assertEquals(true, res.get("success"));
        List objs = (List)res.get("data");
        // .count, .bucket_count, .qps
        Assert.assertEquals(3, objs.size());

        Response cartsResponse = target("metrics/carts111").request().accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        res = cartsResponse.readEntity(Map.class);

        Assert.assertEquals(true, res.get("success"));
        objs = (List)res.get("data");
        Assert.assertEquals(12, objs.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testGetSpecificMetric() {
        Counter c1 = MetricManager.getCounter("dubbo", MetricName.build("middleware.dubbo.provider.qps"));
        c1.inc();
        Meter m1 = MetricManager.getMeter("carts", MetricName.build("shared.carts.my_cart"));
        m1.mark();
        Meter m2 = MetricManager.getMeter("carts", MetricName.build("shared.carts.my_cart").tagged("source", "taobao"));
        m2.mark(10);

        Response dubboResponse = target("metrics/specific").queryParam("metric", "shared.carts.my_cart.m1")
                .request().accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        Map<String, Object> res = dubboResponse.readEntity(Map.class);

        Assert.assertEquals(true, res.get("success"));
        List<LinkedHashMap<String, Object>> objs = (List<LinkedHashMap<String, Object>>)res.get("data");
        Assert.assertEquals(2, objs.size());
        LinkedHashMap<String, Object> metricObj = objs.get(0);
        Assert.assertEquals(metricObj.get("metric"), "shared.carts.my_cart.m1");
    }

    @Test
    public void testCompassAddonMatch() {

        Set<String> names = new HashSet<String>();
        names.add("shared.carts.my_cart.hit.count");

        MetricName mn = MetricName.build("shared.carts.my_cart");
        Compass compass = new CompassImpl();
        Compass.Context context = compass.time();
        context.markAddon("hit");
        context.stop();

        MetricNameSetFilter filter = new MetricNameSetFilter(names);

        Assert.assertTrue(filter.matches(mn, compass));
    }

    @Test
    public void testCollectCompassMetric() {
        MetricsCollector collector = MetricsCollectorFactory.createNew(TimeUnit.SECONDS.toSeconds(1),
                1.0 / TimeUnit.MILLISECONDS.toNanos(1), null);
        Assert.assertTrue(collector instanceof NormalMetricsCollector);
        // create a compass with 1 second bucket and a manual clock
        ManualClock clock = new ManualClock();
        Compass compass = new CompassImpl(ReservoirType.EXPONENTIALLY_DECAYING, clock, 10, 1, 10, 10);
        Compass.Context context = compass.time();
        context.markAddon("hit");
        clock.addSeconds(1);
        context.success();
        context.stop();
        collector.collect(MetricName.build("TEST"), compass, System.currentTimeMillis());
        List<MetricObject> metricObjects = collector.build();
        Assert.assertEquals(21, metricObjects.size());
        MetricObject found = null;
        for (MetricObject object: metricObjects) {
            if (object.getMetric().equals("TEST.hit.count")) {
                found = object;
            }
        }
        Assert.assertNotNull("Should have the .hit.count metric", found);

        // search for the instant count
        found = null;
        for (MetricObject object: metricObjects) {
            if (object.getMetric().equals("TEST.bucket_count")) {
                found = object;
            }
        }
        Assert.assertNotNull("Should have the .bucket_count metric", found);
        Assert.assertEquals(found.getMetricType(), MetricObject.MetricType.DELTA);

        // search for the instant count
        found = null;
        for (MetricObject object: metricObjects) {
            if (object.getMetric().equals("TEST.success_bucket_count")) {
                found = object;
            }
        }
        Assert.assertNotNull("Should have the .success_bucket_count metric", found);
        Assert.assertEquals(found.getMetricType(), MetricObject.MetricType.DELTA);

        // search for the instant count
        found = null;
        for (MetricObject object: metricObjects) {
            if (object.getMetric().equals("TEST.hit_bucket_count")) {
                found = object;
            }
        }
        Assert.assertNotNull("Should have the .hit_bucket_count metric", found);
        Assert.assertEquals(found.getMetricType(), MetricObject.MetricType.DELTA);
    }

    @Test
    public void testCounterBucketCount() {
        Counter c1 = MetricManager.getCounter("dubbo",
                MetricName.build("aaa.dubbo.provider").level(MetricLevel.NORMAL));
        c1.inc(10);
        System.out.println("phase 1: " + System.currentTimeMillis());
        assertResponse("aaa.dubbo.provider.count", 10L);
        System.out.println("phase 2: " + System.currentTimeMillis());
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("phase 3: " + System.currentTimeMillis());
        assertResponse("aaa.dubbo.provider.bucket_count", 10L);
        System.out.println("phase 4: " + System.currentTimeMillis());
    }

    @Test
    public void testCompassBucketCount() {
        Compass c1 = MetricManager.getCompass("dubbo",
                MetricName.build("bbb.dubbo.provider").level(MetricLevel.NORMAL), ReservoirType.BUCKET);
        Compass.Context context = c1.time();
        context.success();
        context.markAddon("hit");
        context.error("timeout");
        context.stop();
        System.out.println("phase 1: " + System.currentTimeMillis());
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("phase 2: " + System.currentTimeMillis());
        assertResponse("bbb.dubbo.provider.bucket_count", 1L);
        assertResponse("bbb.dubbo.provider.success_bucket_count", 1L);
        assertResponse("bbb.dubbo.provider.hit_bucket_count", 1L);
        assertResponse("bbb.dubbo.provider.error_bucket_count", 1L);
        assertResponse("bbb.dubbo.provider.hit_rate", 1.0d);
        System.out.println("phase 3: " + System.currentTimeMillis());
    }

    @Test
    public void testCompatibility() {
        Compass c1 = MetricManager.getCompass("dubbo",
                MetricName.build("ccc.dubbo.provider").level(MetricLevel.NORMAL), ReservoirType.BUCKET);
        Compass.Context context = c1.time();
        context.success();
        context.stop();

        System.out.println("phase 1: " + System.currentTimeMillis());
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("phase 2: " + System.currentTimeMillis());
        assertResponse("ccc.dubbo.provider.qps", 0.06667d);
        System.out.println("phase 3: " + System.currentTimeMillis());
        assertResponse("ccc.dubbo.provider.success_rate", 1.0d);
        System.out.println("phase 4: " + System.currentTimeMillis());
    }

    @Test
    public void testFastCompass() {
        FastCompass c1 = MetricManager.getFastCompass("dubbo", MetricName.build("eee.dubbo.provider").level(MetricLevel.NORMAL));
        c1.record(10, "success");
        c1.record(20, "fail");

        System.out.println("phase 1: " + System.currentTimeMillis());
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("phase 2: " + System.currentTimeMillis());
        assertResponse("eee.dubbo.provider.qps", 0.133333d);
        System.out.println("phase 3: " + System.currentTimeMillis());
        assertResponse("eee.dubbo.provider.success_rate", 0.5d);
        assertResponse("eee.dubbo.provider.rt", 15.0d);
        assertResponse("eee.dubbo.provider.bucket_count", 2L);
        System.out.println("phase 4: " + System.currentTimeMillis());
    }

    @Test
    public void testTairSpecialCase() {
        FastCompass c1 = MetricManager.getFastCompass("tair", MetricName.build("mw.tair.read").level(MetricLevel.NORMAL));
        c1.record(10, "hit");
        c1.record(20, "success");
        c1.record(30, "error");

        System.out.println("phase 1: " + System.currentTimeMillis());
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("phase 2: " + System.currentTimeMillis());
        assertResponse("mw.tair.read.qps", 0.2d);
        System.out.println("phase 3: " + System.currentTimeMillis());
        assertResponse("mw.tair.read.success_rate", 0.6666d);
        assertResponse("mw.tair.read.rt", 20.0d);
        assertResponse("mw.tair.read.bucket_count", 3L);
        System.out.println("phase 4: " + System.currentTimeMillis());
    }

    @SuppressWarnings("unchecked")
    private void assertResponse(String metric, Object value) {
        Response dubboResponse = target("metrics/specific")
                .queryParam("metric", metric)
                .request().accept(MediaType.APPLICATION_JSON_TYPE).get(Response.class);
        Map<String, Object> res = dubboResponse.readEntity(Map.class);

        Assert.assertEquals(true, res.get("success"));
        List<LinkedHashMap<String, Object>> objs = (List<LinkedHashMap<String, Object>>)res.get("data");
        Assert.assertEquals(1, objs.size());
        LinkedHashMap<String, Object> metricObj = objs.get(0);
        Assert.assertEquals(metric, metricObj.get("metric"));
        if (metricObj.get("value") instanceof Integer) {
            Assert.assertEquals(value, new Long((Integer) metricObj.get("value")));
        } else if (metricObj.get("value") instanceof Double) {
            Assert.assertEquals((Double)value, (Double)metricObj.get("value"), 0.001d);
        }
    }
}
