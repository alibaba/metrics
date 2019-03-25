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
package com.alibaba.metrics.rest;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static Response buildResult(Object data, boolean success, String message) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (data != null) {
            result.put("data", data);
        }
        result.put("success", success);
        result.put("timestamp", System.currentTimeMillis());
        result.put("message", message);

        return Response.ok(result).build();
    }

    public static Map<String, Object> buildResultPojo(Object data, boolean success, String message) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (data != null) {
            result.put("data", data);
        }
        result.put("success", success);
        result.put("timestamp", System.currentTimeMillis());
        result.put("message", message);
        return result;
    }

    public static Response buildResult(Object data) {
        return buildResult(data, true, "");
    }

    public static boolean checkZero(Object o){

        if (o == null){
            return true;
        }

        if (o instanceof Long && ((Long) o ) == 0) {
            return true;
        }

        if (o instanceof Integer && ((Integer) o ) == 0) {
            return true;
        }

        if (o instanceof Double && ((Double) o ) == 0) {
            return true;
        }

        if (o instanceof Float && ((Float) o ) == 0){
            return true;
        }

        return false;
    }

}
