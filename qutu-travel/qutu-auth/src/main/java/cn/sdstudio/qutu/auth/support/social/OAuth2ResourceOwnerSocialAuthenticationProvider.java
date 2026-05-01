package cn.sdstudio.qutu.auth.support.social;

import cn.sdstudio.qutu.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationProvider;
import cn.sdstudio.qutu.common.core.constant.SecurityConstants;
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
 * 社交登录的核心处理
 *
 * @author qutu
 */
public class OAuth2ResourceOwnerSocialAuthenticationProvider
		extends OAuth2ResourceOwnerBaseAuthenticationProvider<OAuth2ResourceOwnerSocialAuthenticationToken> {

	public OAuth2ResourceOwnerSocialAuthenticationProvider(AuthenticationManager authenticationManager,
			OAuth2AuthorizationService authorizationService,
			OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
		super(authenticationManager, authorizationService, tokenGenerator);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return OAuth2ResourceOwnerSocialAuthenticationToken.class.isAssignableFrom(authentication);
	}

	@Override
	public void checkClient(RegisteredClient registeredClient) {
		assert registeredClient != null;
		if (!registeredClient.getAuthorizationGrantTypes()
			.contains(new AuthorizationGrantType(SecurityConstants.SOCIAL))) {
			throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
		}
	}

	@Override
	public UsernamePasswordAuthenticationToken buildToken(Map<String, Object> reqParameters) {
		// 社交登录时，前端传入的code对应的用户标识作为principal
		// 在social模式下，code参数已通过converter校验
		// 实际的微信code->openid转换在UserDetailsService中处理
		// 这里用 social:MINI:{code} 格式作为username，由UserDetailsService解析
		String code = (String) reqParameters.get(SecurityConstants.SOCIAL_PARAMETER_NAME);
		String state = (String) reqParameters.get(SecurityConstants.SOCIAL_STATE_PARAMETER_NAME);
		if (state == null) {
			state = "MINI";
		}
		// 使用特殊前缀标记社交登录，UserDetailsService据此区分处理
		String principal = "social:" + state + ":" + code;
		return new UsernamePasswordAuthenticationToken(principal, null);
	}

}
