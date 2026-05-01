package cn.sdstudio.qutu.story.controller;

import cn.sdstudio.qutu.common.core.util.R;
import cn.sdstudio.qutu.common.security.util.SecurityUtils;
import cn.sdstudio.qutu.story.dto.StoryCreateDTO;
import cn.sdstudio.qutu.story.service.StoryService;
import cn.sdstudio.qutu.story.vo.StoryDetailVO;
import cn.sdstudio.qutu.story.vo.StoryListVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stories")
@Tag(name = "旅行故事", description = "旅行故事接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class StoryController {

    private final StoryService storyService;

    @Operation(summary = "分页获取我的故事", description = "按日期筛选（可选）")
    @GetMapping("/page")
    public R<?> page(@ParameterObject Page<StoryListVO> page,
                     @Parameter(description = "记录日期（yyyy-MM-dd）") @RequestParam(required = false) String date) {
        Long userId = null;
        try {
            userId = Objects.requireNonNull(SecurityUtils.getUser()).getId();
        } catch (Exception ignored) {
        }
        if (userId == null) {
            Page<StoryListVO> empty = new Page<>(page.getCurrent(), page.getSize());
            return R.ok(empty);
        }
        LocalDate recordDate = null;
        if (date != null && !date.isBlank()) {
            try {
                recordDate = LocalDate.parse(date);
            } catch (Exception ignored) {
            }
        }
        return R.ok(storyService.pageStories(page, userId, recordDate));
    }

    @Operation(summary = "故事详情")
    @GetMapping("/{id}")
    public R<StoryDetailVO> detail(@PathVariable Long id) {
        Long userId = null;
        try {
            userId = Objects.requireNonNull(SecurityUtils.getUser()).getId();
        } catch (Exception ignored) {
        }
        if (userId == null) {
            return R.failed("请先登录");
        }
        return R.ok(storyService.getDetail(id, userId));
    }

    @Operation(summary = "创建故事")
    @PostMapping
    public R<Long> create(@RequestBody StoryCreateDTO dto) {
        Long userId = null;
        try {
            userId = Objects.requireNonNull(SecurityUtils.getUser()).getId();
        } catch (Exception ignored) {
        }
        if (userId == null) {
            return R.failed("请先登录");
        }
        return R.ok(storyService.createStory(userId, dto));
    }

    @Operation(summary = "更新故事")
    @PutMapping("/{id}")
    public R<Boolean> update(@PathVariable Long id, @RequestBody StoryCreateDTO dto) {
        Long userId = null;
        try {
            userId = Objects.requireNonNull(SecurityUtils.getUser()).getId();
        } catch (Exception ignored) {
        }
        if (userId == null) {
            return R.failed("请先登录");
        }
        return R.ok(storyService.updateStory(id, userId, dto));
    }

    @Operation(summary = "删除故事")
    @DeleteMapping("/{id}")
    public R<Boolean> delete(@PathVariable Long id) {
        Long userId = null;
        try {
            userId = Objects.requireNonNull(SecurityUtils.getUser()).getId();
        } catch (Exception ignored) {
        }
        if (userId == null) {
            return R.failed("请先登录");
        }
        return R.ok(storyService.deleteStory(id, userId));
    }
}
