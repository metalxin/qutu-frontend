package cn.sdstudio.qutu.admin.api.feign;

import cn.sdstudio.qutu.admin.api.entity.SysDictItem;
import cn.sdstudio.qutu.common.core.constant.ServiceNameConstants;
import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.feign.annotation.NoToken;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 远程字典服务接口
 *
 * @author lengleng
 * @date 2025/05/30
 */
@FeignClient(contextId = "remoteDictService", value = ServiceNameConstants.UPMS_SERVICE,
		path = "${qutu.feign.path-prefix:}")
public interface RemoteDictService {

	/**
	 * 通过字典类型查找字典
	 * @param type 字典类型
	 * @return 同类型字典
	 */
	@NoToken
	@GetMapping("/dict/remote/type/{type}")
	R<List<SysDictItem>> getDictByType(@PathVariable("type") String type);

}
