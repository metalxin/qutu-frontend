package cn.sdstudio.qutu.planning.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.planning.dto.PlanningRequestDTO;
import cn.sdstudio.qutu.planning.service.PlanningService;
import cn.sdstudio.qutu.planning.vo.AIRouteVO;
import cn.sdstudio.qutu.planning.vo.PreferenceOptionVO;
import cn.sdstudio.qutu.planning.vo.TransportModeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/planning")
@Tag(name = "智能行程规划", description = "AI智能行程规划相关接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class PlanningController {

	private final PlanningService planningService;

	@Operation(summary = "生成智能规划路线", description = "根据用户输入的出发地、目的地、天数、偏好等参数，AI生成行程规划")
	@PostMapping("/generate")
	public R<AIRouteVO> generateRoute(@RequestBody PlanningRequestDTO request) {
		if (request.getStartLocation() == null || request.getStartLocation().isEmpty()) {
			return R.failed("请选择出发地");
		}
		if (request.getEndLocation() == null || request.getEndLocation().isEmpty()) {
			return R.failed("请选择目的地");
		}
		if (request.getDays() == null || request.getDays() < 1) {
			return R.failed("行程天数至少1天");
		}
		if (request.getPreference() == null || request.getPreference().isEmpty()) {
			request.setPreference("auto");
		}
		AIRouteVO route = planningService.generateRoute(request);
		return R.ok(route);
	}

	@Operation(summary = "获取规划偏好选项", description = "获取所有可用的规划偏好选项")
	@GetMapping("/preferences")
	public R<List<PreferenceOptionVO>> getPreferenceOptions() {
		return R.ok(planningService.getPreferenceOptions());
	}

	@Operation(summary = "获取交通方式", description = "获取所有可用的交通方式")
	@GetMapping("/transports")
	public R<List<TransportModeVO>> getTransportModes() {
		return R.ok(planningService.getTransportModes());
	}

	@Operation(summary = "获取热门城市", description = "获取热门旅游城市列表")
	@GetMapping("/cities")
	public R<List<String>> getHotCities() {
		return R.ok(planningService.getHotCities());
	}

	@Operation(summary = "获取规划设置", description = "获取默认规划设置")
	@GetMapping("/settings")
	public R<?> getPlanningSettings() {
		return R.ok(java.util.Map.of("defaultStartLocation", "合肥市", "defaultEndLocation", "合肥市", "defaultDays", 3,
				"defaultPreference", "auto"));
	}

}
