package cn.sdstudio.qutu.admin.controller;

import cn.sdstudio.qutu.admin.service.SysEmailService;
import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.security.annotation.Inner;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.regex.Pattern;

/**
 * 邮箱验证码控制器
 *
 * @author qutu
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
@Tag(description = "email", name = "邮箱验证码模块")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class SysEmailController {

	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

	private final SysEmailService emailService;

	/**
	 * 发送邮箱验证码
	 * @param email 邮箱地址
	 * @return 发送结果
	 */
	@Inner(value = false)
	@PostMapping("/code")
	public R<Boolean> sendEmailCode(@RequestParam String email) {
		if (StrUtil.isBlank(email) || !EMAIL_PATTERN.matcher(email).matches()) {
			return R.failed("邮箱地址不合法");
		}
		return emailService.sendEmailCode(email);
	}
}
