/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
