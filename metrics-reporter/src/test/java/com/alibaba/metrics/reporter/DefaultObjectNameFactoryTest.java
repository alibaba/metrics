package com.alibaba.metrics.reporter;


import com.alibaba.metrics.MetricName;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import javax.management.ObjectName;

public class DefaultObjectNameFactoryTest {

	@Test
	public void createsObjectNameWithDomainInInput() {
		DefaultObjectNameFactory f = new DefaultObjectNameFactory();
		ObjectName on = f.createName("type", "com.domain", MetricName.build("something.with.dots"));
		Assertions.assertThat(on.getDomain()).isEqualTo("com.domain");
	}

	@Test
	public void createsObjectNameWithNameAsKeyPropertyName() {
		DefaultObjectNameFactory f = new DefaultObjectNameFactory();
		ObjectName on = f.createName("type", "com.domain", MetricName.build("something.with.dots"));
		Assertions.assertThat(on.getKeyProperty("name")).isEqualTo("something.with.dots");
	}
}
