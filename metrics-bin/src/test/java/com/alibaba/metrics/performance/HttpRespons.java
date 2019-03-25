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
package com.alibaba.metrics.performance;
import java.util.Vector;

/**
 * HTTP响应对象
 *
 */
public class HttpRespons {

    String urlString;

    int defaultPort;

    String file;

    String host;

    String path;

    int port;

    String protocol;

    String query;

    String ref;

    String userInfo;

    String contentEncoding;

    String content;

    String contentType;

    int code;

    String message;

    String method;

    int connectTimeout;

    int readTimeout;

    Vector<String> contentCollection;

    public String getContent() {
        return content;
    }

    public String getContentType() {
        return contentType;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Vector<String> getContentCollection() {
        return contentCollection;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public String getMethod() {
        return method;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public String getUrlString() {
        return urlString;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public String getFile() {
        return file;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public int getPort() {
        return port;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getQuery() {
        return query;
    }

    public String getRef() {
        return ref;
    }

    public String getUserInfo() {
        return userInfo;
    }

}
