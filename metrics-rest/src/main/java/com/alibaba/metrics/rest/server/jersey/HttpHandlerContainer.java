package com.alibaba.metrics.rest.server.jersey;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsExchange;
import org.glassfish.jersey.internal.MapPropertiesDelegate;
import org.glassfish.jersey.jdkhttp.internal.LocalizationMessages;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerException;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.ConfigHelper;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpHandlerContainer implements HttpHandler, Container {

    private static final Logger LOGGER = Logger.getLogger(HttpHandlerContainer.class.getName());

    private volatile ApplicationHandler appHandler;
    private volatile ContainerLifecycleListener containerListener;

    /**
     * Creates a new Container connected to given {@link ApplicationHandler Jersey application}.
     *
     * @param appHandler Jersey application handler for which the container should be
     *                   initialized.
     */
    HttpHandlerContainer(ApplicationHandler appHandler) {
        this.appHandler = appHandler;
        this.containerListener = ConfigHelper.getContainerLifecycleListener(appHandler);
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        /**
         * This is a URI that contains the path, query and fragment components.
         */
        URI exchangeUri = exchange.getRequestURI();

        /**
         * The base path specified by the HTTP context of the HTTP handler. It
         * is in decoded form.
         */
        String decodedBasePath = exchange.getHttpContext().getPath();

        // Ensure that the base path ends with a '/'
        if (!decodedBasePath.endsWith("/")) {
            if (decodedBasePath.equals(exchangeUri.getPath())) {
                /**
                 * This is an edge case where the request path does not end in a
                 * '/' and is equal to the context path of the HTTP handler.
                 * Both the request path and base path need to end in a '/'
                 * Currently the request path is modified.
                 *
                 * TODO support redirection in accordance with resource configuration feature.
                 */
                exchangeUri = UriBuilder.fromUri(exchangeUri).
                        path("/").build();
            }
            decodedBasePath += "/";
        }

        /*
         * The following is madness, there is no easy way to get the complete
         * URI of the HTTP request!!
         *
         * TODO this is missing the user information component, how can this be obtained?
         */
        final boolean isSecure = exchange instanceof HttpsExchange;
        String scheme = isSecure ? "https" : "http";

        URI baseUri;
        try {
            List<String> hostHeader = exchange.getRequestHeaders().get("Host");
            if (hostHeader != null) {
                StringBuilder sb = new StringBuilder(scheme);
                sb.append("://").append(hostHeader.get(0)).append(decodedBasePath);
                baseUri = new URI(sb.toString());
            } else {
                InetSocketAddress addr = exchange.getLocalAddress();
                baseUri = new URI(scheme, null, addr.getHostName(), addr.getPort(),
                        decodedBasePath, null, null);
            }
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(ex);
        }

        final URI requestUri = baseUri.resolve(exchangeUri);

        final ResponseWriter responseWriter = new ResponseWriter(exchange);
        ContainerRequest requestContext = new ContainerRequest(baseUri, requestUri,
                exchange.getRequestMethod(), getSecurityContext(exchange.getPrincipal(), isSecure),
                new MapPropertiesDelegate());
        requestContext.setEntityStream(exchange.getRequestBody());
        requestContext.getHeaders().putAll(exchange.getRequestHeaders());
        requestContext.setWriter(responseWriter);
        try {
            appHandler.handle(requestContext);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error handling request: ", e);
        } finally {
            // if the response was not committed yet by the JerseyApplication
            // then commit it and log warning
            responseWriter.closeAndLogWarning();
        }
    }

    private SecurityContext getSecurityContext(final Principal principal, final boolean isSecure) {
        return new SecurityContext() {

            @Override
            public boolean isUserInRole(String role) {
                return false;
            }

            @Override
            public boolean isSecure() {
                return isSecure;
            }

            @Override
            public Principal getUserPrincipal() {
                return principal;
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        };
    }

    @Override
    public ResourceConfig getConfiguration() {
        return appHandler.getConfiguration();
    }

    @Override
    public void reload() {
        reload(getConfiguration());
    }

    @Override
    public void reload(ResourceConfig configuration) {
        containerListener.onShutdown(this);
        appHandler = new ApplicationHandler(configuration);
        containerListener = ConfigHelper.getContainerLifecycleListener(appHandler);
        containerListener.onReload(this);
        containerListener.onStartup(this);
    }

    /**
     * Inform this container that the server has been started.
     *
     * This method must be implicitly called after the server containing this container is started.
     */
    void onServerStart() {
        this.containerListener.onStartup(this);
    }

    /**
     * Inform this container that the server is being stopped.
     *
     * This method must be implicitly called before the server containing this container is stopped.
     */
    void onServerStop() {
        this.containerListener.onShutdown(this);
    }

    private final static class ResponseWriter implements ContainerResponseWriter {

        private final HttpExchange exchange;
        private final AtomicBoolean closed;

        /**
         * Creates a new ResponseWriter for given {@link HttpExchange HTTP Exchange}.
         *
         * @param exchange Exchange of the {@link HttpServer JDK Http Server}
         */
        ResponseWriter(HttpExchange exchange) {
            this.exchange = exchange;
            this.closed = new AtomicBoolean(false);
        }

        @Override
        public OutputStream writeResponseStatusAndHeaders(long contentLength, ContainerResponse context)
                throws ContainerException {
            final MultivaluedMap<String, String> responseHeaders = context.getStringHeaders();
            final Headers serverHeaders = exchange.getResponseHeaders();
            for (final Map.Entry<String, List<String>> e : responseHeaders.entrySet()) {
                for (String value : e.getValue()) {
                    serverHeaders.add(e.getKey(), value);
                }
            }

            try {
                if (context.getStatus() == 204) {
                    // Work around bug in LW HTTP server
                    // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6886436
                    exchange.sendResponseHeaders(context.getStatus(), -1);
                } else {
                    exchange.sendResponseHeaders(context.getStatus(),
                            getResponseLength(contentLength));
                }
            } catch (IOException ioe) {
                throw new ContainerException("Error during writing out the response headers.", ioe);
            }

            return exchange.getResponseBody();
        }

        private long getResponseLength(long contentLength) {
            if (contentLength == 0) {
                return -1;
            }
            if (contentLength < 0) {
                return 0;
            }
            return contentLength;
        }

        @Override
        public boolean suspend(long timeOut, TimeUnit timeUnit, TimeoutHandler timeoutHandler) {
            throw new UnsupportedOperationException("Method suspend is not support by the container.");
        }

        @Override
        public void setSuspendTimeout(long timeOut, TimeUnit timeUnit) throws IllegalStateException {
            throw new UnsupportedOperationException("Method suspend is not support by the container.");
        }

        @Override
        public void failure(Throwable error) {
            try {
                exchange.sendResponseHeaders(500, getResponseLength(0));
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Unable to send a failure response.", e);
            } finally {
                commit();
                rethrow(error);
            }
        }

        @Override
        public boolean enableResponseBuffering() {
            return true;
        }

        @Override
        public void commit() {
            if (closed.compareAndSet(false, true)) {
                exchange.close();
            }
        }

        /**
         * Rethrow the original exception as required by JAX-RS, 3.3.4
         *
         * @param error throwable to be re-thrown
         */
        private void rethrow(Throwable error) {
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                throw new ContainerException(error);
            }
        }

        /**
         * Commits the response and logs a warning message.
         *
         * This method should be called by the container at the end of the
         * handle method to make sure that the ResponseWriter was committed.
         */
        private void closeAndLogWarning() {
            if (closed.compareAndSet(false, true)) {
                exchange.close();
                LOGGER.log(Level.WARNING, LocalizationMessages.ERROR_RESPONSEWRITER_RESPONSE_UNCOMMITED());
            }
        }
    }

}
