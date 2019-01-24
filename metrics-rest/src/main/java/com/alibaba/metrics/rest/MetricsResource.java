package com.alibaba.metrics.rest;

import com.alibaba.fastjson.JSON;
import com.alibaba.metrics.ClusterHistogram;
import com.alibaba.metrics.Compass;
import com.alibaba.metrics.Counter;
import com.alibaba.metrics.FastCompass;
import com.alibaba.metrics.Gauge;
import com.alibaba.metrics.Histogram;
import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.Meter;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;
import com.alibaba.metrics.ReservoirType;
import com.alibaba.metrics.Timer;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import com.alibaba.metrics.common.filter.MetricNameSetFilter;
import com.alibaba.metrics.server.MetricsSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import static com.alibaba.metrics.rest.Utils.buildResult;
import static com.alibaba.metrics.rest.Utils.buildResultPojo;

@Path("/metrics")
public class MetricsResource {

    private final static Logger logger = LoggerFactory.getLogger(MetricsResource.class);

    private static IMetricManager manager = MetricManager.getIMetricManager();
    private static final double rateFactor = TimeUnit.SECONDS.toSeconds(1);
    private static final double durationFactor = 1.0 / TimeUnit.MILLISECONDS.toNanos(1);
    private static final String MULTI_GROUP_DELIM = ",";

    private static final MetricName baseName = new MetricName("middleware.metrics.rest.url");
    /**
     * list接口序列化MetricObject时过滤掉timestamp/value
     */
    private static final MetricObjectPropertyFilter filter = new MetricObjectPropertyFilter();

    @Path("/list")
    @GET
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    public Response listMetrics() {
        if (manager.isEnabled()) {
            Map<String, Set<MetricObject>> metrics = new LinkedHashMap<String, Set<MetricObject>>();
            for (String groupName : manager.listMetricGroups()) {
                MetricRegistry registry = manager.getMetricRegistryByGroup(groupName);
                Set<MetricObject> metricsPerRegistry = new LinkedHashSet<MetricObject>();
                metricsPerRegistry.addAll(buildMetricRegistry(registry));
                metrics.put(groupName, metricsPerRegistry);
            }
            try {
                String data = JSON.toJSONString(buildResultPojo(metrics, true, ""), filter);
                return Response.ok(data).build();
            } catch (Exception e) {
                return buildResult(null, false, e.toString());
            }
        } else {
            return buildResult(null, false, "Metrics has been disabled explicitly!");
        }
    }

    @GET
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    public Response getMetrics() {
        return getMetrics("all");
    }

    @GET
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    @Path("/{group}")
    public Response getMetrics(@DefaultValue("all") @PathParam("group") String group) {

        MetricName name = baseName.tagged("url", "/" + group).level(MetricLevel.TRIVIAL);
        Timer urlTimer = manager.getTimer("metrics", name, ReservoirType.BUCKET);
        Timer.Context context = urlTimer.time();

        try {
            if (!manager.isEnabled()) {
                return buildResult(null, false, "Metrics has been disabled explicitly!");
            }

            Map<String, List<MetricObject>> metricsData = new TreeMap<String, List<MetricObject>>();

            if ("all".equalsIgnoreCase(group) || group == null) {
                for (String groupName : manager.listMetricGroups()) {
                    try {
                        MetricRegistry registry = manager.getMetricRegistryByGroup(groupName);
                        metricsData.put(groupName, buildMetricRegistry(registry));

                    } catch (Throwable e) {
                        return buildResult(null, false, e.toString());
                    }
                }

                return buildResult(metricsData, true, "");

            } else if (group.contains(MULTI_GROUP_DELIM)) {

                String[] groups = group.split(MULTI_GROUP_DELIM);

                for (String groupName : groups) {
                    try {
                        // FIXME manager.getMetricRegistryByGroup will create one if group does not exist
                        if (!manager.listMetricGroups().contains(groupName)) {
                            continue;
                        }
                        MetricRegistry registry = manager.getMetricRegistryByGroup(groupName);
                        metricsData.put(groupName, buildMetricRegistry(registry));
                    } catch (Throwable e) {
                        return buildResult(null, false, e.toString());
                    }
                }

                return buildResult(metricsData, true, "");

            } else {
                // FIXME manager.getMetricRegistryByGroup will create one if group does not exist
                List<String> groups = manager.listMetricGroups();
                if (!groups.contains(group)) {
                    return buildResult(null, false, "The specified group is not found!");
                }
                MetricRegistry registry = manager.getMetricRegistryByGroup(group);
                return buildResult(buildMetricRegistry(registry), true, "");
            }

        } finally {
            context.stop();
        }
    }

    @GET
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    @Path("/{group}/level/{level}")
    public Response getMetricByLevel(final @PathParam("group") String group,
                                     final @PathParam("level") String level,
                                     final @QueryParam("above") boolean above) {
        if (!manager.isEnabled()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        if (group.contains(MULTI_GROUP_DELIM)) {
            Map<String, List<MetricObject>> metricsData = new TreeMap<String, List<MetricObject>>();

            String[] groups = group.split(MULTI_GROUP_DELIM);

            String[] levels = level.split(MULTI_GROUP_DELIM);

            for (int i = 0; i < groups.length; i++) {
                try {
                    // FIXME manager.getMetricRegistryByGroup will create one if group does not exist
                    if (!manager.listMetricGroups().contains(groups[i])) {
                        continue;
                    }
                    MetricRegistry registry = manager.getMetricRegistryByGroup(groups[i]);
                    MetricLevelFilter levelFilter = new MetricLevelFilter(MetricLevel.valueOf(levels[i]), above);
                    metricsData.put(groups[i], buildMetricRegistry(registry, levelFilter));
                } catch (Throwable e) {
                    return buildResult(null, false, e.toString());
                }
            }
            return buildResult(metricsData, true, "");

        } else {
            // FIXME manager.getMetricRegistryByGroup will create one if group does not exist
            List<String> groups = manager.listMetricGroups();

            if (!groups.contains(group)) {
                return buildResult(null, false, "The specified group is not found!");
            }

            MetricRegistry registry = manager.getMetricRegistryByGroup(group);

            try {
                MetricLevelFilter levelFilter = new MetricLevelFilter(MetricLevel.valueOf(level), above);
                List<MetricObject> metricObjects = buildMetricRegistry(registry, levelFilter);
                if (metricObjects.isEmpty()) {
                    return buildResult(null, false, "No metric matching the specified level found!");
                }
                return buildResult(metricObjects);
            } catch (IllegalArgumentException e) {
                return buildResult(null, false, e.toString());
            }
        }
    }

    @GET
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    @Path("/{group}/{metric}")
    public Response getMetric(final @PathParam("group") String group,
                              final @PathParam("metric") String metricName,
                              final @QueryParam("tagKey") List<String> tagKeys,
                              final @QueryParam("tagValue") List<String> tagValues) {

        if (!manager.isEnabled()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        List<String> groups = manager.listMetricGroups();
        // FIXME manager.getMetricRegistryByGroup will create one if group does not exist
        if (!groups.contains(group)) {
            return buildResult(null, false, "The specified group is not found!");
        }

        MetricRegistry registry = manager.getMetricRegistryByGroup(group);

        List<MetricObject> metricObjects = buildMetricRegistry(registry, new MetricNameSetFilter(metricName));

        if (metricObjects.isEmpty()) {
            return buildResult(null, false, "The specified metric is not found!");
        }

        return buildResult(metricObjects);
    }

    @GET
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    @Path("/specific")
    public Response getMetric(final @QueryParam("metric") Set<String> metricNames, @QueryParam("zeroIgnore") boolean zeroIgnore) {

        if (!manager.isEnabled()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        MetricName name = baseName.tagged("url", "/specific").level(MetricLevel.TRIVIAL);
        Timer urlTimer = manager.getTimer("metrics", name, ReservoirType.BUCKET);
        Timer.Context context = urlTimer.time();
        try {
            return getMetricsInternal(metricNames, zeroIgnore);
        } finally {
            context.stop();
        }
    }

    @POST
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    @Path("/specific")
    public Response getMetricsByPost(MultivaluedMap<String, String> params, @QueryParam("zeroIgnore") boolean zeroIgnore) {
        final Set<String> metricNames = new HashSet<String>(params.get("metric"));
        return getMetric(metricNames, zeroIgnore);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    @Path("/search")
    public Response searchMetrics(String metricsSearch, @HeaderParam("REMOTE_ADDR") String remoteAddr,
            @HeaderParam("X-Forwarded-For") String xForwardedFor, @Context HttpServletRequest httpServletRequest) {

        if (httpServletRequest != null){
            logger.info("httpheader REMOTE_ADDR {}, httpheader X-Forwarded-For {};socket remoteaddr {}, port {}",
                    remoteAddr, xForwardedFor, httpServletRequest.getRemoteAddr(), httpServletRequest.getRemotePort());
        }else{
            logger.info("httpheader REMOTE_ADDR {}, httpheader X-Forwarded-For {}，httpServletRequest is null",
                    remoteAddr, xForwardedFor);
        }


        if (!manager.isEnabled()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        MetricName name = baseName.tagged("url", "/search").level(MetricLevel.TRIVIAL);
        Timer urlTimer = manager.getTimer("metrics", name, ReservoirType.BUCKET);
        Timer.Context context = urlTimer.time();
        try {
            MetricsSearchService service = MetricsSearchService.getInstance();
            return Response.ok(service.search(metricsSearch)).build();
        } finally {
            context.stop();
        }
    }

    private Response getMetricsInternal(final Set<String> metricNames, boolean zeroIgnore) {
        // FIXME manager.getMetricRegistryByGroup will create one if group does not exist

        List<String> groups = manager.listMetricGroups();

        MetricFilter keyFilter = new MetricNameSetFilter(metricNames);

        List<MetricObject> metricObjects = new ArrayList<MetricObject>();

        for (String group : groups) {
            MetricRegistry registry = manager.getMetricRegistryByGroup(group);
            metricObjects.addAll(buildMetricRegistry(registry, keyFilter));
        }

        if (metricObjects.isEmpty()) {
            return buildResult(null, false, "The specified metric is not found!");
        }

        if (zeroIgnore){
            List<MetricObject> allMetricObjects = metricObjects;
            metricObjects = new ArrayList<MetricObject>();

            for(MetricObject o : allMetricObjects){
                if ((o != null) && (!Utils.checkZero(o.getValue()))){
                    metricObjects.add(o);
                }
            }
        }

        return buildResult(metricObjects);
    }

    private List<MetricObject> buildMetricRegistry(MetricRegistry registry) {
        return buildMetricRegistry(registry, null);
    }

    private List<MetricObject> buildMetricRegistry(MetricRegistry registry, MetricFilter filter) {

        long ts = System.currentTimeMillis();

        MetricsCollector collector =
                MetricsCollectorFactory.createNew(CollectLevel.NORMAL, rateFactor, durationFactor, filter);

        SortedMap<MetricName, Gauge> gauges = filter == null ?
                registry.getGauges() : registry.getGauges(filter);
        for (Map.Entry<MetricName, Gauge> entry : gauges.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), ts);
        }

        SortedMap<MetricName, Counter> counters = filter == null ?
                registry.getCounters() : registry.getCounters(filter);
        for (Map.Entry<MetricName, Counter> entry : counters.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), ts);
        }

        SortedMap<MetricName, Meter> meters = filter == null ?
                registry.getMeters() : registry.getMeters(filter);
        for (Map.Entry<MetricName, Meter> entry : meters.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), ts);
        }

        SortedMap<MetricName, Histogram> histograms = filter == null ?
                registry.getHistograms() : registry.getHistograms(filter);
        for (Map.Entry<MetricName, Histogram> entry : histograms.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), ts);
        }

        SortedMap<MetricName, Timer> timers = filter == null ?
                registry.getTimers() : registry.getTimers(filter);
        for (Map.Entry<MetricName, Timer> entry : timers.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), ts);
        }

        SortedMap<MetricName, Compass> compasses = filter == null ?
                registry.getCompasses() : registry.getCompasses(filter);
        for (Map.Entry<MetricName, Compass> entry : compasses.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), ts);
        }

        SortedMap<MetricName, FastCompass> fastCompasses = filter == null ?
                registry.getFastCompasses() : registry.getFastCompasses(filter);
        for (Map.Entry<MetricName, FastCompass> entry : fastCompasses.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), ts);
        }

        SortedMap<MetricName, ClusterHistogram> clusterHistograms = filter == null ?
                registry.getClusterHistograms(MetricFilter.ALL) : registry.getClusterHistograms(filter);
        for (Map.Entry<MetricName, ClusterHistogram> entry : clusterHistograms.entrySet()) {
            collector.collect(entry.getKey(), entry.getValue(), ts);
        }

        return collector.build();
    }

}
