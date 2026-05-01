package cn.sdstudio.qutu.admin.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 社交登录用户查询DTO
 *
 * @author qutu
 */
@Data
@Schema(description = "社交登录用户查询")
public class SocialUserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 微信小程序openid
	 */
	@Schema(description = "微信小程序openid")
	private String miniOpenid;

	/**
	 * 微信openid
	 */
	@Schema(description = "微信openid")
	private String wxOpenid;

}
