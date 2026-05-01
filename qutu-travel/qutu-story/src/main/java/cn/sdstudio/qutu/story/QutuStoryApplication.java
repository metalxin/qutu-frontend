package cn.sdstudio.qutu.story;

import cn.sdstudio.qutu.common.feign.annotation.EnableQutuFeignClients;
import cn.sdstudio.qutu.common.security.annotation.EnableQutuResourceServer;
import cn.sdstudio.qutu.common.swagger.annotation.EnableQutuDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableQutuDoc("story")
@EnableQutuFeignClients
@EnableQutuResourceServer
@EnableDiscoveryClient
@SpringBootApplication
public class QutuStoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(QutuStoryApplication.class, args);
    }
}
