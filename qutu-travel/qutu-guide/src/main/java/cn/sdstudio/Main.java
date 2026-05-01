package cn.sdstudio;

import cn.sdstudio.qutu.common.feign.annotation.EnableQutuFeignClients;
import cn.sdstudio.qutu.common.security.annotation.EnableQutuResourceServer;
import cn.sdstudio.qutu.common.swagger.annotation.EnableQutuDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableQutuDoc("guide")
@EnableQutuFeignClients
@EnableQutuResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
