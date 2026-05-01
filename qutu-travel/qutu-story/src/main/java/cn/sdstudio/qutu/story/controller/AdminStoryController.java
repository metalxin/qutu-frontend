package cn.sdstudio.qutu.story.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.log.annotation.SysLog;
import cn.sdstudio.qutu.common.security.annotation.HasPermission;
import cn.sdstudio.qutu.story.dto.StoryAdminDTO;
import cn.sdstudio.qutu.story.dto.StoryQueryDTO;
import cn.sdstudio.qutu.story.service.StoryService;
import cn.sdstudio.qutu.story.vo.StoryAdminDetailVO;
import cn.sdstudio.qutu.story.vo.StoryAdminVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

/**
 * 旅行故事后台管理
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/story-admin")
@Tag(name = "故事后台", description = "旅行故事后台管理接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class AdminStoryController {

    private final StoryService storyService;

    @Operation(summary = "故事分页", description = "后台分页查询所有用户故事")
    @GetMapping("/page")
    public R<?> pageStories(@ParameterObject Page<StoryAdminVO> page,
                            @ParameterObject StoryQueryDTO query) {
        return R.ok(storyService.pageAdminStories(page, query));
    }

    @Operation(summary = "故事详情", description = "获取故事详情（含图片）")
    @GetMapping("/{id}")
    public R<StoryAdminDetailVO> getStory(@Parameter(description = "故事ID") @PathVariable Long id) {
        return R.ok(storyService.getAdminDetail(id));
    }

    @Operation(summary = "新增故事", description = "后台新增故事")
    @SysLog("新增故事")
    @PostMapping
    @HasPermission("story_add")
    public R<Long> saveStory(@RequestBody StoryAdminDTO dto) {
        return R.ok(storyService.createAdminStory(dto));
    }

    @Operation(summary = "修改故事", description = "后台修改故事")
    @SysLog("修改故事")
    @PutMapping
    @HasPermission("story_edit")
    public R<Boolean> updateStory(@RequestBody StoryAdminDTO dto) {
        return R.ok(storyService.updateAdminStory(dto));
    }

    @Operation(summary = "删除故事", description = "后台删除故事")
    @SysLog("删除故事")
    @DeleteMapping("/{id}")
    @HasPermission("story_del")
    public R<Boolean> deleteStory(@Parameter(description = "故事ID") @PathVariable Long id) {
        return R.ok(storyService.removeAdminStory(id));
    }
}
