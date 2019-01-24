package com.alibaba.metrics.nginx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetUtils {

    /**
     * Implementation Notes:
     *   sun.net.www.protocol.http.HttpURLConnection does not allow overriding the `Host` header,
     *   although we -Dsun.net.http.allowRestrictedHeaders=true to work around this issue,
     *   it is a global configuration and may raise potential security concerns for other components.
     *   Therefore we use the plain socket to get the http response, which is equivalent to:
     *   curl -v http://127.0.0.1:80/nginx_status -H "Host: 127.0.0.1"
     * @param nginxHost the nginx host, normally will be localhost
     * @param port the nginx status port, normally will be 80
     * @param statusPath the nginx status path
     * @param statusHost the nginx status host
     * @return the nginx stats, for example:
     *          Active connections: 1
                server accepts handled requests request_time
                 23 23 23 9818
                Reading: 0 Writing: 1 Waiting: 0
     */
    public static Response request(String nginxHost, int port, String statusPath, String statusHost) {
        BufferedReader br = null;
        Socket s = new Socket();
        try {
            s.connect(new InetSocketAddress(nginxHost, port), 100);
            OutputStream outputStream = s.getOutputStream();
            StringBuilder requestPayload = new StringBuilder();
            requestPayload.append("GET ").append(statusPath).append(" HTTP/1.1\r\n")
                    .append("Host: ").append(statusHost).append("\r\n")
                    .append("User-Agent: Ali-metrics Java Client\r\n")
                    .append("Connection: close\r\n")
                    .append("\r\n");
            outputStream.write(requestPayload.toString().getBytes());
            outputStream.flush();
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            boolean start = false;
            boolean success = false;
            while ((line = br.readLine()) != null) {
                if (start) {
                    sb.append(line).append("\n");
                }
                if ("".equals(line)) {
                    start = true;
                }
                if ("HTTP/1.1 200 OK".equals(line)) {
                    success = true;
                }
            }
            String result = sb.toString().trim();
            return new Response(result, success);
        } catch (Exception e) {
            return new Response(e.getMessage(), false);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    // ignore
                }
            }
            try {
                s.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public static class Response {

        private boolean success;
        private String content;

        public Response(String content, boolean success) {
            this.success = success;
            this.content = content;
        }

        public Response(String content) {
            this.content = content;
            this.success = true;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getContent() {
            return content;
        }
    }
}
