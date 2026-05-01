package cn.sdstudio.qutu.destination.service;

import cn.sdstudio.qutu.destination.entity.SpotTip;
import cn.sdstudio.qutu.destination.vo.TipVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 景点提示Service
 *
 * @author qutu
 * @date 2026-02-02
 */
public interface SpotTipService extends IService<SpotTip> {

    /**
     * 获取景点提示列表
     */
    List<TipVO> getSpotTips(Long spotId);
}
