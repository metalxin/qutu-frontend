package cn.sdstudio.qutu.admin.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.sdstudio.qutu.admin.service.SysEmailService;
import cn.sdstudio.qutu.common.core.constant.CacheConstants;
import cn.sdstudio.qutu.common.core.constant.SecurityConstants;
import cn.sdstudio.qutu.common.core.util.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 邮箱验证码服务实现
 *
 * @author qutu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysEmailServiceImpl implements SysEmailService {

	private final RedisTemplate redisTemplate;

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String fromAddress;

	/**
	 * 发送邮箱验证码
	 */
	private static final String CODE_COOLDOWN_KEY = "EMAIL_CODE_COOLDOWN:";

	private static final long CODE_COOLDOWN_SECONDS = 60;

	@Override
	public R<Boolean> sendEmailCode(String email) {
		Object cooldownObj = redisTemplate.opsForValue().get(CODE_COOLDOWN_KEY + email);
		if (cooldownObj != null) {
			log.info("邮箱验证码冷却中:{}", email);
			return R.ok(Boolean.FALSE, "验证码已发送，请稍后再试");
		}

		String code = RandomUtil.randomNumbers(Integer.parseInt(SecurityConstants.CODE_SIZE));
		log.info("邮箱生成验证码成功:{},{}", email, code);

		redisTemplate.opsForValue()
			.set(CacheConstants.DEFAULT_CODE_KEY + email, code, SecurityConstants.CODE_TIME, TimeUnit.SECONDS);

		redisTemplate.opsForValue()
			.set(CODE_COOLDOWN_KEY + email, "1", CODE_COOLDOWN_SECONDS, TimeUnit.SECONDS);

		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromAddress);
			message.setTo(email);
			message.setSubject("趣途旅行 - 邮箱验证码");
			message.setText("您的验证码为：" + code + "，有效期" + SecurityConstants.CODE_TIME + "秒，请勿泄露给他人。");
			mailSender.send(message);
			log.info("邮箱验证码发送成功:{}", email);
		} catch (Exception e) {
			log.error("邮箱验证码发送失败:{}", email, e);
			// 发送失败时删除已存入的验证码
			redisTemplate.delete(CacheConstants.DEFAULT_CODE_KEY + email);
			return R.failed("验证码发送失败，请检查邮箱地址");
		}

		return R.ok(Boolean.TRUE, "验证码已发送");
	}

	/**
	 * 校验邮箱验证码
	 */
	@Override
	public boolean verifyEmailCode(String email, String code) {
		return verifyEmailCode(email, code, true);
	}

	@Override
	public boolean verifyEmailCode(String email, String code, boolean deleteAfterVerify) {
		if (code == null || code.isEmpty()) {
			return false;
		}
		String key = CacheConstants.DEFAULT_CODE_KEY + email;
		Object codeObj = redisTemplate.opsForValue().get(key);
		if (codeObj == null) {
			return false;
		}
		String savedCode = codeObj.toString();
		if (code.equals(savedCode)) {
			if (deleteAfterVerify) {
				redisTemplate.delete(key);
			}
			return true;
		}
		return false;
	}
}
