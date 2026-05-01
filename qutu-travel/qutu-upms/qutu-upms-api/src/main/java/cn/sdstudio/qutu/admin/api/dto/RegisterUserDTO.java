package cn.sdstudio.qutu.admin.api.dto;

import lombok.Data;

/**
 * 注册用户 DTO
 *
 * @author lengleng
 * @date 2024/12/23
 */
@Data
public class RegisterUserDTO {

	/**
	 * 用户名
	 */
	private String username;

	/**
	 * 新密码
	 */
	private String password;

	/**
	 * 电话
	 */
	private String phone;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 验证码
	 */
	private String code;

	/**
	 * 随机字符串/邮箱地址（验证码校验键）
	 */
	private String randomStr;
}
