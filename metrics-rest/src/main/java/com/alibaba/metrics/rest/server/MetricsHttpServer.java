/*
 * Copyright 2017 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */

package com.alibaba.metrics.rest.server;

import com.alibaba.metrics.rest.MetricsController;
import com.alibaba.metrics.rest.MetricsResource;
import com.alibaba.metrics.rest.server.jersey.FastJsonFeature;
import com.alibaba.metrics.rest.server.jersey.HttpServerFactory;
import com.alibaba.metrics.rest.server.jersey.JerseyEventListener;
import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.TracingConfig;

import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MetricsHttpServer {

    /**
     * 默认端口
     */
    private static final int DEFAULT_PORT = 8006;
    private static final int DEFAULT_CORE_POOL_SIZE = 10;
    private static final int DEFAULT_MAX_POOL_SIZE = 100;
    private static final long DEFAULT_MAX_ALIVE_TIMEOUT = 0L;
    private static final int DEFAULT_MAX_QUEUE_SIZE = 5000;
    private static final String DEFAULT_BINDING_HOST = "0.0.0.0";
    private static final String ALI_METRICS_BINDING_HOST = "com.alibaba.metrics.http.binding.host";
    private static final String ALI_METRICS_HTTP_PORT = "com.alibaba.metrics.http.port";

    /**
     * 监听端口
     */
    private int port;

    /**
     * The core pool size of http server's thread pool
     */
    private final int corePoolSize;

    /**
     * The max pool size of http server's thread pool
     */
    private final int maxPoolSize;

    /**
     * The keep alive timeout of http server's thread pool
     */
    private final long keepAliveTimeout;

    /**
     * The max queue size of http server's thread pool
     */
    private final int maxQueueSize;

    /**
     * HttpServer
     */
    private HttpServer httpServer;

    private ResourceConfig resourceConfig = new ResourceConfig();

    public MetricsHttpServer() {
        port = Integer.getInteger(ALI_METRICS_HTTP_PORT, DEFAULT_PORT);
        this.corePoolSize = DEFAULT_CORE_POOL_SIZE;
        this.maxPoolSize = DEFAULT_MAX_POOL_SIZE;
        this.keepAliveTimeout = DEFAULT_MAX_ALIVE_TIMEOUT;
        this.maxQueueSize = DEFAULT_MAX_QUEUE_SIZE;
    }

    public MetricsHttpServer(int port, int corePoolSize, int maxPoolSize, long keepAliveTimeout, int maxQueueSize) {
        this.port = port;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.keepAliveTimeout = keepAliveTimeout;
        this.maxQueueSize = maxQueueSize;
    }

    public void start() {
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(getPort()).build();

        Logger jerseyLogger = Logger.getLogger("org.glassfish.jersey");
        jerseyLogger.setLevel(Level.WARNING);

        resourceConfig.register(MetricsResource.class)
                .register(MetricsController.class)
                .property(ServerProperties.TRACING, TracingConfig.ON_DEMAND.name())
//                .register(new LoggingFilter(jerseyLogger, false))
                .register(new LoggingFeature(jerseyLogger))
                .register(JerseyEventListener.class)
                .register(FastJsonFeature.class);


        String bindingHost = System.getProperty(ALI_METRICS_BINDING_HOST, DEFAULT_BINDING_HOST);

        // set up map request response time for http server
        // http server will run a dedicated thread to check if the connection are expired and close them
        if (System.getProperty("sun.net.httpserver.maxReqTime") == null) {
            // set max time in seconds to wait for a request to finished
            System.setProperty("sun.net.httpserver.maxReqTime", "30");
        }
        if (System.getProperty("sun.net.httpserver.maxRspTime") == null) {
            // set max time in seconds to wait for a response to finished
            System.setProperty("sun.net.httpserver.maxRspTime", "30");
        }

        try {
            Class fastJsonAutoDiscoverClass = MetricsHttpServer.class.getClassLoader()
                    .loadClass("com.alibaba.fastjson.support.jaxrs.FastJsonAutoDiscoverable");
            Field autoDscoverField = fastJsonAutoDiscoverClass.getField("autoDiscover");
            autoDscoverField.set(null, false);
        } catch (Exception e) {
            // ignore
        }

        httpServer = HttpServerFactory.createHttpServer(baseUri, bindingHost, corePoolSize, maxPoolSize,
                keepAliveTimeout, maxQueueSize, resourceConfig);
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public ResourceConfig getResourceConfig() {
        return resourceConfig;
    }

    public void setResourceConfig(ResourceConfig resourceConfig) {
        this.resourceConfig = resourceConfig;
    }
}
