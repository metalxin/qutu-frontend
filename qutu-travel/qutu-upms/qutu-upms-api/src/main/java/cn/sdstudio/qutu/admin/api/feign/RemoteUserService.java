/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the qutu.sdstudio.cn developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package cn.sdstudio.qutu.admin.api.feign;

import cn.sdstudio.qutu.admin.api.dto.SocialUserDTO;
import cn.sdstudio.qutu.admin.api.dto.UserDTO;
import cn.sdstudio.qutu.admin.api.dto.UserInfo;
import cn.sdstudio.qutu.common.core.constant.ServiceNameConstants;
import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 远程用户服务接口：提供用户信息查询功能
 *
 * @author lengleng
 * @date 2025/05/30
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.UPMS_SERVICE,
		path = "${qutu.feign.path-prefix:}")
public interface RemoteUserService {

	/**
	 * (未登录状态调用，需要加 @NoToken) 通过用户名查询用户、角色信息
	 * @param user 用户查询对象
	 * @return R
	 */
	@NoToken
	@GetMapping("/user/info/query")
	R<UserInfo> info(@SpringQueryMap UserDTO user);

	/**
	 * (未登录状态调用，需要加 @NoToken) 通过社交标识查询用户信息
	 * @param miniOpenid 微信小程序openid
	 * @param wxOpenid 微信openid
	 * @return R
	 */
	@NoToken
	@GetMapping("/user/info/social")
	R<UserInfo> socialInfo(@SpringQueryMap SocialUserDTO socialUser);

}
