package cn.sdstudio.qutu.admin.service;

import cn.sdstudio.qutu.common.core.util.R;

/**
 * 邮箱验证码服务接口
 *
 * @author qutu
 */
public interface SysEmailService {

	/**
	 * 发送邮箱验证码
	 * @param email 邮箱地址
	 * @return 发送结果
	 */
	R<Boolean> sendEmailCode(String email);

	/**
	 * 校验邮箱验证码
	 * @param email 邮箱地址
	 * @param code 验证码
	 * @return 是否校验通过
	 */
	boolean verifyEmailCode(String email, String code);

	boolean verifyEmailCode(String email, String code, boolean deleteAfterVerify);
}
