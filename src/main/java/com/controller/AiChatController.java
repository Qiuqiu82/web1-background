package com.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/ai/chat")
public class AiChatController {

    @Value("${ai.rag.base-url:http://127.0.0.1:8000}")
    private String ragBaseUrl;

    @Value("${ai.rag.connect-timeout-ms:5000}")
    private int connectTimeoutMs;

    @Value("${ai.rag.read-timeout-ms:30000}")
    private int readTimeoutMs;

    @PostMapping("/send")
    public R send(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = request.getSession() != null ? (Long) request.getSession().getAttribute("userId") : null;
        if (userId == null) {
            return R.error(401, "请先登录");
        }

        String message = str(body.get("message")).trim();
        if (StringUtils.isBlank(message)) {
            return R.error(400, "message不能为空");
        }

        String sourcePage = str(body.get("sourcePage")).trim();
        String sessionId = str(body.get("sessionId")).trim();
        if (StringUtils.isBlank(sessionId)) {
            sessionId = "ai_" + UUID.randomUUID().toString().replace("-", "");
        }

        Map<String, Object> ragReq = new HashMap<String, Object>();
        ragReq.put("question", message);
        ragReq.put("stream", false);
        ragReq.put("sessionId", sessionId);
        ragReq.put("sourcePage", sourcePage);
        ragReq.put("userId", userId);

        String answer;
        try {
            String responseBody = doJsonPost(buildQueryUrl(), JSON.toJSONString(ragReq));
            answer = parseAnswer(responseBody);
        } catch (Exception e) {
            return R.error(500, "AI服务调用失败: " + e.getMessage());
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("sessionId", sessionId);
        data.put("answer", StringUtils.defaultIfBlank(answer, "暂时没有生成回复，请稍后再试"));
        data.put("recommendations", new ArrayList<Object>());
        return R.ok().put("data", data);
    }

    private String buildQueryUrl() {
        String base = StringUtils.defaultIfBlank(ragBaseUrl, "http://127.0.0.1:8000").trim();
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + "/api/query";
    }

    private String parseAnswer(String responseBody) {
        if (StringUtils.isBlank(responseBody)) {
            return "";
        }

        JSONObject json = JSON.parseObject(responseBody);
        String answer = json.getString("answer");
        if (StringUtils.isNotBlank(answer)) {
            return answer;
        }

        JSONObject data = json.getJSONObject("data");
        if (data != null) {
            answer = data.getString("answer");
            if (StringUtils.isNotBlank(answer)) {
                return answer;
            }
        }

        String detail = json.getString("detail");
        if (StringUtils.isNotBlank(detail)) {
            return detail;
        }

        return responseBody;
    }

    private String doJsonPost(String url, String body) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL target = new URL(url);
            connection = (HttpURLConnection) target.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");

            byte[] payload = body.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(payload.length);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(payload);
                os.flush();
            }

            int code = connection.getResponseCode();
            InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
            String responseText = readStream(stream);
            if (code < 200 || code >= 300) {
                throw new RuntimeException("HTTP " + code + " " + responseText);
            }
            return responseText;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private String readStream(InputStream stream) throws Exception {
        if (stream == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private String str(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
