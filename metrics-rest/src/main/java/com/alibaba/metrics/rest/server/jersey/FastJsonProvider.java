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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.AfterFilter;
import com.alibaba.fastjson.serializer.BeforeFilter;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.PropertyPreFilter;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * from https://github.com/smallnest/fastjson-jaxrs-json-provider.git
 */
@Provider
public class FastJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {
    private boolean annotated = false;
    private String[] scanpackages = null;
    private Class<?>[] clazzes = null;

    @javax.ws.rs.core.Context
    javax.ws.rs.core.UriInfo uriInfo;

    //禁止fastjson的循环引用
    private FastJsonConfig fastJsonConfig = new FastJsonConfig(new SerializeConfig(),
            new SerializerFeature[]{SerializerFeature.DisableCircularReferenceDetect});

    /**
     * Can serialize/deserialize all types.
     */
    public FastJsonProvider() {

    }

    /**
     * Only serialize/deserialize all types annotated with
     * {@link FastJsonType}.
     */
    public FastJsonProvider(boolean annotated) {
        this.annotated = annotated;
    }

    /**
     * Only serialize/deserialize all types in scanpackages.
     */
    public FastJsonProvider(String[] scanpackages) {
        this.scanpackages = scanpackages;
    }

    /**
     * Only serialize/deserialize all types in scanpackages.
     */
    public FastJsonProvider(String[] scanpackages, boolean annotated) {
        this.scanpackages = scanpackages;
        this.annotated = annotated;
    }

    /**
     * Only serialize/deserialize all types in clazzes.
     */
    public FastJsonProvider(Class<?>[] clazzes) {
        this.clazzes = clazzes;
    }

    /**
     * Init this provider with more fastjson configurations.
     */
    public FastJsonProvider init(FastJsonConfig fastJsonConfig) {
        this.fastJsonConfig = fastJsonConfig;
        return this;
    }


    /**
     * Check whether a class can be serialized or deserialized. It can check
     * based on packages, annotations on entities or explicit classes.
     */
    protected boolean isValidType(Class<?> type, Annotation[] classAnnotations) {
        if (type == null)
            return false;

        if (annotated) {
            return checkAnnotation(type);
        } else if (scanpackages != null) {
            String classPackage = type.getPackage().getName();
            for (String pkg : scanpackages) {
                if (classPackage.startsWith(pkg)) {
                    return checkAnnotation(type);
                }
            }
            return false;
        } else if (clazzes != null) {
            for (Class<?> cls : clazzes) { // must strictly equal. Don't check
                // inheritance
                if (cls == type)
                    return true;
            }

            return false;
        }

        return true;
    }

    private boolean checkAnnotation(Class<?> type) {
        Annotation[] annotations = type.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation instanceof FastJsonType) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check media type like "application/json".
     *
     * @param mediaType media type
     * @return true if the media type is valid
     */
    protected boolean hasMatchingMediaType(MediaType mediaType) {
        if (mediaType != null) {
            String subtype = mediaType.getSubtype();
            return "json".equalsIgnoreCase(subtype) || subtype.endsWith("+json") ||
                    "javascript".equalsIgnoreCase(subtype) || "x-javascript".equalsIgnoreCase(subtype);
        }
        return true;
    }

    public String toJSONString(Object object, SerializeFilter filter, SerializerFeature[] features) {
        SerializeWriter out = new SerializeWriter();

        try {
            JSONSerializer serializer = new JSONSerializer(out, fastJsonConfig.serializeConfig);
            if (features != null) {
                for (SerializerFeature feature : features) {
                    serializer.config(feature, true);
                }
            }

            if (filter != null) {
                if (filter instanceof PropertyPreFilter) {
                    serializer.getPropertyPreFilters().add((PropertyPreFilter) filter);
                }

                if (filter instanceof NameFilter) {
                    serializer.getNameFilters().add((NameFilter) filter);
                }

                if (filter instanceof ValueFilter) {
                    serializer.getValueFilters().add((ValueFilter) filter);
                }

                if (filter instanceof PropertyFilter) {
                    serializer.getPropertyFilters().add((PropertyFilter) filter);
                }

                if (filter instanceof BeforeFilter) {
                    serializer.getBeforeFilters().add((BeforeFilter) filter);
                }

                if (filter instanceof AfterFilter) {
                    serializer.getAfterFilters().add((AfterFilter) filter);
                }
            }

            serializer.write(object);

            return out.toString();
        } finally {
            out.close();
        }
    }



    /**
     * Method that JAX-RS container calls to try to check whether given value
     * (of specified type) can be serialized by this provider.
     */
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!hasMatchingMediaType(mediaType)) {
            return false;
        }

        return isValidType(type, annotations);
    }

    /**
     * Method that JAX-RS container calls to try to figure out serialized length
     * of given value. always return -1 to denote "not known".
     */
    public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    /**
     * Method that JAX-RS container calls to serialize given value.
     */
    public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        SerializeFilter filter = null;

        // check pretty parameter
        if (uriInfo != null) {
            List<String> prettyList = uriInfo.getQueryParameters().get("pretty");
            if (prettyList != null && !prettyList.isEmpty() && !prettyList.get(0).equalsIgnoreCase("false")) {
                if (fastJsonConfig.serializerFeatures == null)
                    fastJsonConfig.serializerFeatures = new SerializerFeature[] { SerializerFeature.PrettyFormat };
                else {
                    List<SerializerFeature> serializerFeatures = Arrays.asList(fastJsonConfig.serializerFeatures);
                    serializerFeatures.add(SerializerFeature.PrettyFormat);
                    fastJsonConfig.serializerFeatures = serializerFeatures.toArray(new SerializerFeature[] {});
                }
            }
        }

        if (fastJsonConfig.serializeFilters != null)
            filter = fastJsonConfig.serializeFilters.get(type);
        String jsonStr = toJSONString(t, filter, fastJsonConfig.serializerFeatures);
        if (jsonStr != null)
            entityStream.write(jsonStr.getBytes("UTF-8"));
    }

    /**
     * Method that JAX-RS container calls to try to check whether values of
     * given type (and media type) can be deserialized by this provider.
     */
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!hasMatchingMediaType(mediaType)) {
            return false;
        }

        return isValidType(type, annotations);
    }

    /**
     * Method that JAX-RS container calls to deserialize given value.
     */
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String input = null;
        try {
            input = inputStreamToString(entityStream);
        } catch (Exception e) {

        }
        if (input == null) {
            return null;
        }
        if (fastJsonConfig.features == null)
            return JSON.parseObject(input, type, fastJsonConfig.parserConfig, JSON.DEFAULT_PARSER_FEATURE);
        else
            return JSON.parseObject(input, type, fastJsonConfig.parserConfig, JSON.DEFAULT_PARSER_FEATURE,
                    fastJsonConfig.features);
    }

    private static String inputStreamToString(InputStream in) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        StringBuilder buffer = new StringBuilder();
        String line = "";
        while ((line = br.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}
