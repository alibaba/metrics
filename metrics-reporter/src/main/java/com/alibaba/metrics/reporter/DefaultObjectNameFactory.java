package com.alibaba.metrics.reporter;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import com.alibaba.metrics.MetricName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultObjectNameFactory implements ObjectNameFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(JmxReporter.class);

	public ObjectName createName(String type, String domain, MetricName metricName) {
		String name = metricName.getKey();
		try {
			ObjectName objectName = new ObjectName(domain, "name", name);
			if (objectName.isPattern()) {
				objectName = new ObjectName(domain, "name", ObjectName.quote(name));
			}
			return objectName;
		} catch (MalformedObjectNameException e) {
			try {
				return new ObjectName(domain, "name", ObjectName.quote(name));
			} catch (MalformedObjectNameException e1) {
				LOGGER.warn("Unable to register {} {}", type, name, e1);
				throw new RuntimeException(e1);
			}
		}
	}

}
