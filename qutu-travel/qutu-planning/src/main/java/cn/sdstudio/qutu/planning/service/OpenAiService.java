package cn.sdstudio.qutu.planning.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.sdstudio.qutu.planning.config.AiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

	private final AiProperties aiProperties;

	private final ObjectMapper objectMapper;

	public String chat(String systemPrompt, String userPrompt) {
		return chat(systemPrompt, userPrompt, aiProperties.getMaxTokens());
	}

	public String chat(String systemPrompt, String userPrompt, int maxTokens) {
		String baseUrl = aiProperties.getBaseUrl();
		String apiKey = aiProperties.getApiKey();
		String model = aiProperties.getModel();
		int timeout = aiProperties.getTimeout();

		log.info("调用OpenAI API: baseUrl={}, model={}, timeout={}s, maxTokens={}", baseUrl, model, timeout, maxTokens);

		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(10 * 1000);
		factory.setReadTimeout(timeout * 1000);
		RestTemplate restTemplate = new RestTemplate(factory);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", model);
		requestBody.put("max_tokens", maxTokens);
		requestBody.put("temperature", aiProperties.getTemperature());
		requestBody.put("stream", false);

		List<Map<String, String>> messages = new ArrayList<>();

		Map<String, String> systemMessage = new HashMap<>();
		systemMessage.put("role", "system");
		systemMessage.put("content", systemPrompt);
		messages.add(systemMessage);

		Map<String, String> userMessage = new HashMap<>();
		userMessage.put("role", "user");
		userMessage.put("content", userPrompt);
		messages.add(userMessage);

		requestBody.put("messages", messages);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		try {
			long startTime = System.currentTimeMillis();
			String url = baseUrl + "/chat/completions";
			String response = restTemplate.postForObject(url, entity, String.class);
			long elapsed = System.currentTimeMillis() - startTime;
			log.info("OpenAI API调用成功, 耗时: {}ms", elapsed);
			return extractContent(response);
		}
		catch (Exception e) {
			log.error("调用OpenAI API失败: {}", e.getMessage());
			throw new RuntimeException("AI服务调用失败: " + e.getMessage(), e);
		}
	}

	private String extractContent(String responseBody) {
		try {
			JsonNode root = objectMapper.readTree(responseBody);
			JsonNode choices = root.get("choices");
			if (choices != null && choices.isArray() && !choices.isEmpty()) {
				JsonNode message = choices.get(0).get("message");
				if (message != null) {
					JsonNode content = message.get("content");
					if (content != null) {
						String text = content.asText();
						return cleanJsonResponse(text);
					}
				}
			}
			log.warn("OpenAI API返回格式异常: {}", responseBody);
			throw new RuntimeException("AI返回格式异常");
		}
		catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			log.error("解析OpenAI API响应失败: {}", e.getMessage(), e);
			throw new RuntimeException("解析AI响应失败: " + e.getMessage(), e);
		}
	}

	private String cleanJsonResponse(String text) {
		String cleaned = text.trim();
		if (cleaned.startsWith("```json")) {
			cleaned = cleaned.substring(7);
		}
		else if (cleaned.startsWith("```")) {
			cleaned = cleaned.substring(3);
		}
		if (cleaned.endsWith("```")) {
			cleaned = cleaned.substring(0, cleaned.length() - 3);
		}
		return cleaned.trim();
	}

}
