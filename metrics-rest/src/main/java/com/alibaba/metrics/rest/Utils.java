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
