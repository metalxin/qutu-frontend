package cn.sdstudio.qutu.destination;

import cn.sdstudio.qutu.common.feign.annotation.EnableQutuFeignClients;
import cn.sdstudio.qutu.common.security.annotation.EnableQutuResourceServer;
import cn.sdstudio.qutu.common.swagger.annotation.EnableQutuDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * 目的地模块启动类
 *
 * @author qutu
 * @date 2026-02-02
 */
@EnableQutuDoc(value = "destination", isMicro = false)
@EnableQutuFeignClients
@EnableQutuResourceServer
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan("cn.sdstudio.qutu")
public class QutuDestinationApplication {

    public static void main(String[] args) {
        SpringApplication.run(QutuDestinationApplication.class, args);
    }
}
