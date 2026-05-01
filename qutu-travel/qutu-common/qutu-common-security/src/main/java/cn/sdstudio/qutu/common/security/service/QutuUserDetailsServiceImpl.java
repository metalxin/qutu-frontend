/*
 * Copyright (c) 2020 qutu4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.sdstudio.qutu.common.security.service;

import cn.hutool.core.util.StrUtil;
import cn.sdstudio.qutu.admin.api.dto.SocialUserDTO;
import cn.sdstudio.qutu.admin.api.dto.UserDTO;
import cn.sdstudio.qutu.admin.api.dto.UserInfo;
import cn.sdstudio.qutu.admin.api.feign.RemoteUserService;
import cn.sdstudio.qutu.common.core.constant.CacheConstants;
import cn.sdstudio.qutu.common.core.constant.SecurityConstants;
import cn.sdstudio.qutu.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户详情服务实现类，提供基于用户名加载用户详情功能
 *
 * @author lengleng
 * @author hccake
 * @date 2025/05/31
 */
@Slf4j
@Primary
@RequiredArgsConstructor
public class QutuUserDetailsServiceImpl implements QutuUserDetailsService {

	private final RemoteUserService remoteUserService;

	private final CacheManager cacheManager;

	/**
	 * 根据用户名加载用户详情
	 * @param username 用户名
	 * @return 用户详情信息
	 * @throws Exception 获取用户信息过程中可能抛出的异常
	 */
	@Override
	@SneakyThrows
	public UserDetails loadUserByUsername(String username) {
		// 处理社交登录: social:STATE:code 格式
		if (username != null && username.startsWith("social:")) {
			return loadUserBySocial(username);
		}

		Cache cache = cacheManager.getCache(CacheConstants.USER_DETAILS);
		if (cache != null && cache.get(username) != null) {
			return (QutuUser) cache.get(username).get();
		}

		UserDTO userDTO = new UserDTO();
		// 邮箱格式使用email字段查询
		if (username != null && username.contains("@")) {
			userDTO.setEmail(username);
		}
		else {
			userDTO.setUsername(username);
		}
		R<UserInfo> result = remoteUserService.info(userDTO);
		UserDetails userDetails = getUserDetails(result);
		if (cache != null) {
			cache.put(username, userDetails);
		}
		return userDetails;
	}

	/**
	 * 社交登录加载用户详情
	 * username格式: social:STATE:code
	 * STATE标识社交类型（如MINI=微信小程序），code为社交登录凭证
	 * @param username 社交登录标识
	 * @return 用户详情信息
	 */
	@SneakyThrows
	private UserDetails loadUserBySocial(String username) {
		String[] parts = username.split(":", 3);
		if (parts.length < 3) {
			throw new IllegalArgumentException("社交登录参数格式错误");
		}
		String state = parts[1];
		String code = parts[2];

		// 根据不同的社交类型处理
		SocialUserDTO socialUserDTO = new SocialUserDTO();
		if ("MINI".equalsIgnoreCase(state)) {
			// 微信小程序登录: code -> openid 的转换应在auth模块完成
			// 这里简化处理，假设前端或上游已将code转换为openid并传入
			// 实际生产中应在此处调用微信API: code -> session_key + openid
			// 目前用code值直接作为miniOpenid查询（需配合前端实现）
			socialUserDTO.setMiniOpenid(code);
		}
		else {
			socialUserDTO.setWxOpenid(code);
		}

		R<UserInfo> result = remoteUserService.socialInfo(socialUserDTO);
		return getUserDetails(result);
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}

}
