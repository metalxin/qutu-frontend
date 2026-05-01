package cn.sdstudio.qutu.auth.support.email;

import cn.hutool.core.util.StrUtil;
import cn.sdstudio.qutu.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationProvider;
import cn.sdstudio.qutu.common.core.constant.CacheConstants;
import cn.sdstudio.qutu.common.core.util.SpringContextHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import java.util.Map;

/**
 * 邮箱验证码登录的核心处理
 *
 * @author qutu
 */
public class OAuth2ResourceOwnerEmailAuthenticationProvider
		extends OAuth2ResourceOwnerBaseAuthenticationProvider<OAuth2ResourceOwnerEmailAuthenticationToken> {

	public OAuth2ResourceOwnerEmailAuthenticationProvider(AuthenticationManager authenticationManager,
	                                                      OAuth2AuthorizationService authorizationService,
	                                                      OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
		super(authenticationManager, authorizationService, tokenGenerator);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return OAuth2ResourceOwnerEmailAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Override
	public void checkClient(RegisteredClient registeredClient) {
		assert registeredClient != null;
		if (!registeredClient.getAuthorizationGrantTypes()
			.contains(new AuthorizationGrantType("email_code"))) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
		}
	}

	@Override
	public UsernamePasswordAuthenticationToken buildToken(Map<String, Object> reqParameters) {
		String email = (String) reqParameters.get(OAuth2ResourceOwnerEmailAuthenticationConverter.EMAIL_PARAMETER_NAME);
		String code = (String) reqParameters.get("code");

		// 校验邮箱验证码
		if (StrUtil.isBlank(code)) {
			throw new OAuth2AuthenticationException(
					new org.springframework.security.oauth2.core.OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
							"验证码不能为空", ""));
		}

		String key = CacheConstants.DEFAULT_CODE_KEY + email;
		RedisTemplate<String, String> redisTemplate = SpringContextHolder.getBean(RedisTemplate.class);

		Object codeObj = redisTemplate.opsForValue().get(key);
		if (codeObj == null || StrUtil.isBlank(codeObj.toString())) {
			throw new OAuth2AuthenticationException(
					new org.springframework.security.oauth2.core.OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
							"验证码不合法或已过期", ""));
		}

		if (!StrUtil.equals(codeObj.toString(), code)) {
			redisTemplate.delete(key);
			throw new OAuth2AuthenticationException(
					new org.springframework.security.oauth2.core.OAuth2Error(OAuth2ErrorCodes.INVALID_REQUEST,
							"验证码错误", ""));
		}

		redisTemplate.delete(key);
		return new UsernamePasswordAuthenticationToken(email, null);
	}
}
