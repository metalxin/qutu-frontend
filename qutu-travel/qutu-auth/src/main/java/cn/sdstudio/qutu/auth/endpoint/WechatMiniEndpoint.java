package cn.sdstudio.qutu.auth.endpoint;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.sdstudio.qutu.common.core.util.R;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 微信小程序登录端点
 * 处理微信小程序code换取openid
 *
 * @author qutu
 */
@Slf4j
@RestController
@RequestMapping("/wechat")
@RequiredArgsConstructor
public class WechatMiniEndpoint {

	private final RedisTemplate redisTemplate;

	private final ObjectMapper objectMapper;

	/**
	 * 微信小程序appid
	 */
	@Value("${wechat.mini.appid:}")
	private String miniAppId;

	/**
	 * 微信小程序secret
	 */
	@Value("${wechat.mini.secret:}")
	private String miniSecret;

	/**
	 * 微信小程序登录凭证校验URL
	 */
	private static final String MINI_CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

	/**
	 * 微信小程序code换取openid
	 * @param code 微信登录凭证
	 * @return 包含openid的响应
	 */
	@GetMapping("/mini/code2session")
	public R<WechatMiniSession> code2Session(@RequestParam String code) {
		if (StrUtil.isBlank(miniAppId) || StrUtil.isBlank(miniSecret)) {
			log.warn("微信小程序appid或secret未配置");
			return R.failed("微信小程序配置未完成");
		}

		String url = String.format(MINI_CODE2SESSION_URL, miniAppId, miniSecret, code);
		try {
			String response = HttpUtil.get(url, 5000);
			JsonNode jsonNode = objectMapper.readTree(response);

			if (jsonNode.has("errcode") && jsonNode.get("errcode").asInt() != 0) {
				log.error("微信小程序code2session失败: {}", response);
				return R.failed("微信登录失败: " + jsonNode.get("errmsg").asText());
			}

			String openid = jsonNode.get("openid").asText();
			String sessionKey = jsonNode.has("session_key") ? jsonNode.get("session_key").asText() : "";
			String unionid = jsonNode.has("unionid") ? jsonNode.get("unionid").asText() : "";

			// 缓存session_key用于后续解密用户信息
			redisTemplate.opsForValue().set("wechat:mini:session:" + openid, sessionKey, 30, TimeUnit.MINUTES);

			WechatMiniSession session = new WechatMiniSession();
			session.setOpenid(openid);
			session.setUnionid(unionid);
			return R.ok(session);
		}
		catch (Exception e) {
			log.error("微信小程序code2session异常", e);
			return R.failed("微信登录异常");
		}
	}

	/**
	 * 微信小程序会话信息
	 */
	@lombok.Data
	public static class WechatMiniSession {

		private String openid;

		private String unionid;

	}

}
