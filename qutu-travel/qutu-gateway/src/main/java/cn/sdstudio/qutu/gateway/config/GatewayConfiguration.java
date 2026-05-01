package cn.sdstudio.qutu.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.sdstudio.qutu.gateway.filter.QutuRequestGlobalFilter;
import cn.sdstudio.qutu.gateway.handler.GlobalExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 网关配置类
 *
 * @author lengleng
 * @date 2025/05/30
 */
@Configuration(proxyBeanMethods = false)
public class GatewayConfiguration {

	/**
	 * 创建qutuRequest全局过滤器
	 * @return qutuRequest全局过滤器
	 */
	@Bean
	public QutuRequestGlobalFilter qutuRequestGlobalFilter() {
		return new QutuRequestGlobalFilter();
	}

	/**
	 * 创建全局异常处理程序
	 * @param objectMapper 对象映射器
	 * @return 全局异常处理程序
	 */
	@Bean
	public GlobalExceptionHandler globalExceptionHandler(ObjectMapper objectMapper) {
		return new GlobalExceptionHandler(objectMapper);
	}

}
