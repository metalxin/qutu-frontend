package cn.sdstudio.qutu.auth.support.social;

import cn.sdstudio.qutu.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationConverter;
import cn.sdstudio.qutu.common.core.constant.SecurityConstants;
import cn.sdstudio.qutu.common.security.util.OAuth2EndpointUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * 社交登录转换器
 *
 * @author qutu
 */
public class OAuth2ResourceOwnerSocialAuthenticationConverter
		extends OAuth2ResourceOwnerBaseAuthenticationConverter<OAuth2ResourceOwnerSocialAuthenticationToken> {

	/**
	 * 是否支持此convert
	 * @param grantType 授权类型
	 * @return
	 */
	@Override
	public boolean support(String grantType) {
		return SecurityConstants.SOCIAL.equals(grantType);
	}

	@Override
	public OAuth2ResourceOwnerSocialAuthenticationToken buildToken(Authentication clientPrincipal,
			Set requestedScopes, Map additionalParameters) {
		return new OAuth2ResourceOwnerSocialAuthenticationToken(
				new AuthorizationGrantType(SecurityConstants.SOCIAL), clientPrincipal, requestedScopes,
				additionalParameters);
	}

	/**
	 * 校验扩展参数 社交登录code必须不为空
	 * @param request 参数列表
	 */
	@Override
	public void checkParams(HttpServletRequest request) {
		MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
		// code (REQUIRED) - 微信小程序登录凭证
		String code = parameters.getFirst(SecurityConstants.SOCIAL_PARAMETER_NAME);
		if (!StringUtils.hasText(code)
				|| parameters.get(SecurityConstants.SOCIAL_PARAMETER_NAME).size() != 1) {
			OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST,
					SecurityConstants.SOCIAL_PARAMETER_NAME,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}
	}

}
