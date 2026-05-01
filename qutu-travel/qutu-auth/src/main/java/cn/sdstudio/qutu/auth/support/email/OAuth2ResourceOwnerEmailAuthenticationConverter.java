package cn.sdstudio.qutu.auth.support.email;

import cn.sdstudio.qutu.auth.support.base.OAuth2ResourceOwnerBaseAuthenticationConverter;
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
 * 邮箱验证码登录转换器
 *
 * @author qutu
 */
public class OAuth2ResourceOwnerEmailAuthenticationConverter
		extends OAuth2ResourceOwnerBaseAuthenticationConverter<OAuth2ResourceOwnerEmailAuthenticationToken> {

	public static final String EMAIL_PARAMETER_NAME = "email";

	@Override
	public boolean support(String grantType) {
		return "email_code".equals(grantType);
	}

	@Override
	public OAuth2ResourceOwnerEmailAuthenticationToken buildToken(Authentication clientPrincipal,
			Set requestedScopes, Map additionalParameters) {
		return new OAuth2ResourceOwnerEmailAuthenticationToken(
				new AuthorizationGrantType("email_code"), clientPrincipal, requestedScopes, additionalParameters);
	}

	@Override
	public void checkParams(HttpServletRequest request) {
		MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
		String email = parameters.getFirst(EMAIL_PARAMETER_NAME);
		if (!StringUtils.hasText(email) || parameters.get(EMAIL_PARAMETER_NAME).size() != 1) {
			OAuth2EndpointUtils.throwError(OAuth2ErrorCodes.INVALID_REQUEST, EMAIL_PARAMETER_NAME,
					OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI);
		}
	}
}
