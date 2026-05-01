package cn.sdstudio.qutu.planning;

import cn.sdstudio.qutu.common.feign.annotation.EnableQutuFeignClients;
import cn.sdstudio.qutu.common.security.annotation.EnableQutuResourceServer;
import cn.sdstudio.qutu.common.swagger.annotation.EnableQutuDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableQutuDoc(value = "planning", isMicro = false)
@EnableQutuFeignClients
@EnableQutuResourceServer
@EnableDiscoveryClient
@EnableAsync
@SpringBootApplication
@ComponentScan("cn.sdstudio.qutu")
public class QutuPlanningApplication {

	public static void main(String[] args) {
		SpringApplication.run(QutuPlanningApplication.class, args);
	}

}
