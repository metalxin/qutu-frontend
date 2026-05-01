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

package cn.sdstudio.qutu.daemon.quartz.task;

import cn.sdstudio.qutu.daemon.quartz.constants.QutuQuartzEnum;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Spring Bean任务演示类
 *
 * @author lengleng
 * @author 郑健楠
 * @date 2025/05/31
 */
@Slf4j
@Component("demo")
public class SpringBeanTaskDemo {

	/**
	 * 演示方法，用于测试Spring Bean
	 * @param para 输入参数
	 * @return 返回任务日志状态成功类型
	 * @throws Exception 可能抛出的异常
	 */
	@SneakyThrows
	public String demoMethod(String para) {
		log.info("测试于:{}，输入参数{}", LocalDateTime.now(), para);
		return QutuQuartzEnum.JOB_LOG_STATUS_SUCCESS.getType();
	}

}
