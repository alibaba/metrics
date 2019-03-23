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
package com.alibaba.metrics.rest.server.jersey;

import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Map;

public class FastJsonConfig {
    public SerializeConfig serializeConfig;
    public ParserConfig parserConfig;
    public SerializerFeature[] serializerFeatures;
    public Feature[] features;
    public Map<Class<?>, SerializeFilter> serializeFilters;

    public FastJsonConfig(SerializeConfig serializeConfig, SerializerFeature[] serializerFeatures) {
        this(serializeConfig, serializerFeatures, null, new ParserConfig(), null);
    }

    public FastJsonConfig(SerializeConfig serializeConfig, SerializerFeature[] serializerFeatures, Map<Class<?>,
            SerializeFilter> serializeFilters) {
        this(serializeConfig, serializerFeatures, serializeFilters, new ParserConfig(), null);
    }

    public FastJsonConfig(ParserConfig parserConfig, Feature[] features) {
        this(new SerializeConfig(), null, null, parserConfig, features);
    }

    public FastJsonConfig(SerializeConfig serializeConfig, ParserConfig parserConfig) {
        this(serializeConfig, null, null, parserConfig, null);
    }

    public FastJsonConfig(SerializeConfig serializeConfig, SerializerFeature[] serializerFeatures,
                          Map<Class<?>, SerializeFilter> serializeFilters, ParserConfig parserConfig,
                          Feature[] features) {
        this.serializeConfig = serializeConfig;
        this.parserConfig = parserConfig;
        this.serializerFeatures = serializerFeatures;
        this.features = features;
        this.serializeFilters = serializeFilters;
    }
}
