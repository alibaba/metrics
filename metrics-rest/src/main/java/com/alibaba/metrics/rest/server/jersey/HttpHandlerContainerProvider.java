package com.alibaba.metrics.rest.server.jersey;

import com.sun.net.httpserver.HttpHandler;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.spi.ContainerProvider;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.Application;

public final class HttpHandlerContainerProvider implements ContainerProvider {

    @Override
    public <T> T createContainer(Class<T> type, Application application) throws ProcessingException {
        if (type != HttpHandler.class && type != HttpHandlerContainer.class) {
            return null;
        }
        return type.cast(new HttpHandlerContainer(application));
    }
}
