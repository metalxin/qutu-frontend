/*
 *    Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * Neither the name of the qutu.sdstudio.cn developer nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Author: lengleng (wangiegie@gmail.com)
 */

package cn.sdstudio.qutu.codegen;

import cn.sdstudio.qutu.common.datasource.annotation.EnableDynamicDataSource;
import cn.sdstudio.qutu.common.feign.annotation.EnableQutuFeignClients;
import cn.sdstudio.qutu.common.security.annotation.EnableQutuResourceServer;
import cn.sdstudio.qutu.common.swagger.annotation.EnableQutuDoc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 代码生成模块应用启动类
 *
 * @author lengleng
 * @date 2025/05/31
 */
@EnableDynamicDataSource
@EnableQutuFeignClients
@EnableQutuDoc("gen")
@EnableDiscoveryClient
@EnableQutuResourceServer
@SpringBootApplication
public class QutuCodeGenApplication {

	public static void main(String[] args) {
		SpringApplication.run(QutuCodeGenApplication.class, args);
	}

}
