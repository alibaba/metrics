package com.alibaba.metrics.rest.server.jersey;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

import javax.ws.rs.ext.Provider;
import java.util.concurrent.atomic.AtomicLong;

@Provider
public class JerseyEventListener implements ApplicationEventListener{

    public static volatile AtomicLong requestCnt = new AtomicLong();

    @Override
    public void onEvent(ApplicationEvent event) {

    }

    @Override
    public RequestEventListener onRequest(RequestEvent requestEvent) {
        requestCnt.incrementAndGet();
        return null;
    }


}
