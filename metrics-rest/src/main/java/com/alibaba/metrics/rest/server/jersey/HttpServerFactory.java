package com.alibaba.metrics.rest.server.jersey;

import com.alibaba.metrics.rest.server.NamedThreadFactory;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import org.glassfish.jersey.jdkhttp.internal.LocalizationMessages;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ProcessingException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * FIXME a work around to support accepting request from customized address.
 * e.g. from localhost
 */
public class HttpServerFactory {

    private HttpServerFactory() {

    }

    /**
     * Creates and starts the {@link HttpServer JDK HttpServer} with the Jersey
     * application deployed on the given {@link URI}, and bind to given address
     */
    public static HttpServer createHttpServer(final URI uri, final String bindingHost,
                                              final int corePoolSize, final int maxPoolSize,
                                              final long keepAliveTime, final int maxQueueSize,
                                              final ResourceConfig configuration)
            throws ProcessingException {
        final HttpHandlerContainer handler =
                ContainerFactory.createContainer(HttpHandlerContainer.class, configuration);
        return createHttpServer(uri, bindingHost, handler, corePoolSize, maxPoolSize, keepAliveTime, maxQueueSize);
    }

    private static HttpServer createHttpServer(final URI uri, final String bindingHost,
                                               final HttpHandlerContainer handler,
                                               final int corePoolSize, final int maxPoolSize,
                                               final long keepAliveTime, final int maxQueueSize) throws ProcessingException {

        if (uri == null) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_CONTAINER_URI_NULL());
        }

        final String scheme = uri.getScheme();
        if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_CONTAINER_URI_SCHEME_UNKNOWN(uri));
        }

        final String path = uri.getPath();
        if (path == null) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_CONTAINER_URI_PATH_NULL(uri));
        } else if (path.length() == 0) {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_CONTAINER_URI_PATH_EMPTY(uri));
        } else if (path.charAt(0) != '/') {
            throw new IllegalArgumentException(LocalizationMessages.ERROR_CONTAINER_URI_PATH_START(uri));
        }

        final int port = (uri.getPort() == -1) ? 80 : uri.getPort();
        final HttpServer server;
        try {
            server = (scheme.equalsIgnoreCase("http"))
                    ? HttpServer.create(new InetSocketAddress(bindingHost, port), 0)
                    : HttpsServer.create(new InetSocketAddress(bindingHost, port), 0);
        } catch (IOException ioe) {
            throw new ProcessingException(LocalizationMessages.ERROR_CONTAINER_EXCEPTION_IO(), ioe);
        }

        // TODO need to investigate the behavior when the queue if full, should we catch the exception?
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(maxQueueSize),
                new NamedThreadFactory("dubbo-metrics-pool"));
        server.setExecutor(threadPoolExecutor);
        server.createContext(path, handler);

        final HttpServer wrapper = new HttpServer() {

            @Override
            public void bind(InetSocketAddress inetSocketAddress, int i) throws IOException {
                server.bind(inetSocketAddress, i);
            }

            @Override
            public void start() {
                server.start();
                handler.onServerStart();
            }

            @Override
            public void setExecutor(Executor executor) {
                server.setExecutor(executor);
            }

            @Override
            public Executor getExecutor() {
                return server.getExecutor();
            }

            @Override
            public void stop(int i) {
                handler.onServerStop();
                server.stop(i);
            }

            @Override
            public HttpContext createContext(String s, HttpHandler httpHandler) {
                return server.createContext(s, httpHandler);
            }

            @Override
            public HttpContext createContext(String s) {
                return server.createContext(s);
            }

            @Override
            public void removeContext(String s) throws IllegalArgumentException {
                server.removeContext(s);
            }

            @Override
            public void removeContext(HttpContext httpContext) {
                server.removeContext(httpContext);
            }

            @Override
            public InetSocketAddress getAddress() {
                return server.getAddress();
            }
        };

        wrapper.start();

        return wrapper;
    }
}
