/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
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
