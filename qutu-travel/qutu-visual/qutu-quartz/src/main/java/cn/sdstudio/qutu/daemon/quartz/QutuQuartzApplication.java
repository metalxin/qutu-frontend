package cn.sdstudio.qutu.daemon.quartz;

import cn.sdstudio.qutu.common.feign.annotation.EnableQutuFeignClients;
import cn.sdstudio.qutu.common.security.annotation.EnableQutuResourceServer;
import cn.sdstudio.qutu.common.swagger.annotation.EnableQutuDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * qutuQuartz应用启动类
 * <p>
 * 集成定时任务、Feign客户端、资源服务及服务发现功能
 *
 * @author lengleng
 * @author frwcloud
 * @date 2025/05/31
 */
@EnableQutuDoc("job")
@EnableQutuFeignClients
@EnableQutuResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class QutuQuartzApplication {

	public static void main(String[] args) {
		SpringApplication.run(QutuQuartzApplication.class, args);
	}

}
