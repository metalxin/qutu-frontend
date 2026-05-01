package cn.sdstudio.qutu.planning.service;

import cn.sdstudio.qutu.planning.dto.PlanningRequestDTO;
import cn.sdstudio.qutu.planning.vo.AIRouteVO;
import cn.sdstudio.qutu.planning.vo.PreferenceOptionVO;
import cn.sdstudio.qutu.planning.vo.TransportModeVO;

import java.util.List;

public interface PlanningService {

	AIRouteVO generateRoute(PlanningRequestDTO request);

	List<PreferenceOptionVO> getPreferenceOptions();

	List<TransportModeVO> getTransportModes();

	List<String> getHotCities();

}
