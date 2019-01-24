package com.alibaba.metrics.rest;

import com.alibaba.metrics.IMetricManager;
import com.alibaba.metrics.Metric;
import com.alibaba.metrics.MetricFilter;
import com.alibaba.metrics.MetricLevel;
import com.alibaba.metrics.MetricManager;
import com.alibaba.metrics.MetricName;
import com.alibaba.metrics.MetricRegistry;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.SortedMap;

import static com.alibaba.metrics.rest.Utils.buildResult;

@Path("/metrics/controller")
public class MetricsController {

    private static IMetricManager manager = MetricManager.getIMetricManager();

    @Path("/changeLevel/{group}/{metricPrefix}/{level}")
    @POST
    @Produces({Constants.PRODUCE_JSON_WITH_QUALITY_SOURCE, MediaType.TEXT_HTML})
    public Response changeMetricLevel(@PathParam("group") String group,
                                                          final @PathParam("metricPrefix") String metricPrefix,
                                                          @PathParam("level") String level) {

        MetricFilter prefixFilter = new MetricFilter() {
            @Override
            public boolean matches(MetricName name, Metric metric) {
                return name.getKey().startsWith(metricPrefix);
            }
        };

        MetricLevel levelToChange;

        try {
            levelToChange = MetricLevel.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            return buildResult(null, false, "Incorrect metric level!");
        }

        if ("all".equalsIgnoreCase(group)) {

            for (String groupName : manager.listMetricGroups()) {
                try {
                    MetricRegistry registry = manager.getMetricRegistryByGroup(groupName);
                    changeMetricLevel(registry, prefixFilter, levelToChange);
                } catch (Throwable e) {
                    return buildResult(null, false, e.getMessage());
                }
            }

            return buildResult(null, true, "");

        } else {
            List<String> groups = manager.listMetricGroups();
            if (!groups.contains(group)) {
                return buildResult(null, false, "The specified group is not found!");
            }
            MetricRegistry registry = manager.getMetricRegistryByGroup(group);
            changeMetricLevel(registry, prefixFilter, levelToChange);
            return buildResult(null, true, "");
        }
    }

    private void changeMetricLevel(MetricRegistry registry, MetricFilter filter, MetricLevel levelToChange) {
        for (SortedMap.Entry<MetricName, Metric> entry: registry.getMetrics(filter).entrySet()) {
            entry.getKey().level(levelToChange);
        }
    }

}
