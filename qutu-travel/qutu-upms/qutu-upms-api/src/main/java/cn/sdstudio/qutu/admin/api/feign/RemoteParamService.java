package cn.sdstudio.qutu.admin.api.feign;

import cn.sdstudio.qutu.common.core.constant.ServiceNameConstants;
import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 远程参数服务接口
 * <p>
 * 通过Feign客户端调用UPMS服务获取参数配置
 * </p>
 *
 * @author lengleng
 * @date 2025/05/30
 * @see FeignClient
 */
@FeignClient(contextId = "remoteParamService", value = ServiceNameConstants.UPMS_SERVICE,
		path = "${qutu.feign.path-prefix:}")
public interface RemoteParamService {

	/**
	 * 通过key 查询参数配置
	 * @param key key
	 * @NoToken 声明成内部调用，避免MQ 等无法调用
	 */
	@NoToken
	@GetMapping("/param/publicValue/{key}")
	R<String> getByKey(@PathVariable("key") String key);

}
