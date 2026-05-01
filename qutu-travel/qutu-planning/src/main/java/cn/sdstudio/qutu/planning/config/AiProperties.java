package cn.sdstudio.qutu.planning.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "qutu.ai")
public class AiProperties {

	private String baseUrl = "http://127.0.0.1:8000/v1";

	private String apiKey = "omlx";

	private String model = "Qwen3.5-4B-MLX-4bit";

	private int timeout = 180;

	private int maxTokens = 2048;

	private double temperature = 0.7;

}
