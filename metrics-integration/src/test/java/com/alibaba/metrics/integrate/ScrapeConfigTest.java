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
package com.alibaba.metrics.integrate;

import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.JSONWriter;
import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScrapeConfigTest {

    @Test
    public void testReadConfig() {
        JSONReader reader = null;
        try {
            reader = new JSONReader(new FileReader("src/test/resources/.metrics_scrape_config"));
            reader.startArray();
            List<ScrapeConfig> configs = new ArrayList<ScrapeConfig>();
            while(reader.hasNext()) {
                ScrapeConfig config = reader.readObject(ScrapeConfig.class);
                configs.add(config);
            }
            reader.endArray();
            reader.close();
            Assert.assertEquals(8006, configs.get(0).getPort());
            Assert.assertEquals(8007, configs.get(1).getPort());
        } catch (FileNotFoundException e) {
            Assert.fail("Error reading config file");
        }
    }

    @Test
    public void testWriteConfig() {
        JSONWriter writer = null;
        JSONReader reader = null;
        try {
            // read in existing config
            reader = new JSONReader(new FileReader("src/test/resources/.metrics_scrape_config"));
            reader.startArray();
            List<ScrapeConfig> configs = new ArrayList<ScrapeConfig>();
            while(reader.hasNext()) {
                ScrapeConfig config = reader.readObject(ScrapeConfig.class);
                configs.add(config);
            }
            reader.endArray();
            reader.close();

            // write all the configs including the new one
            ScrapeConfig newConfig = new ScrapeConfig(9000, "http://127.0.0.1:9000/metrics/search");
            newConfig.build();
            configs.add(newConfig);
            writer = new JSONWriter(new FileWriter("/tmp/.ali_metrics_scrape_write_test"));
            writer.startArray();
            for (ScrapeConfig config: configs) {
                writer.writeValue(config);
            }
            writer.endArray();
            writer.close();

            // read again and verify it
            reader = new JSONReader(new FileReader("/tmp/.ali_metrics_scrape_write_test"));
            reader.startArray();
            List<ScrapeConfig> configs2 = new ArrayList<ScrapeConfig>();
            while(reader.hasNext()) {
                ScrapeConfig config = reader.readObject(ScrapeConfig.class);
                configs2.add(config);
            }
            reader.endArray();
            reader.close();
            Assert.assertEquals(8006, configs2.get(0).getPort());
            Assert.assertEquals(8007, configs2.get(1).getPort());
            Assert.assertEquals(9000, configs2.get(2).getPort());
        } catch (IOException e) {
            Assert.fail("Error reading/writing config file");
        }
    }
}
