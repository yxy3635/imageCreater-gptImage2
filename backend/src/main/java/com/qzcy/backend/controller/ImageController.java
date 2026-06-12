package com.qzcy.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzcy.backend.config.security.JwtUserPrincipal;
import com.qzcy.backend.dto.ApiResponse;
import com.qzcy.backend.dto.GenerateDto;
import com.qzcy.backend.dto.ImageGenerationConfigDto;
import com.qzcy.backend.entity.ImageRecord;
import com.qzcy.backend.service.ImageGenerationConfigService;
import com.qzcy.backend.service.ImageService;
import com.qzcy.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final ImageGenerationConfigService configService;

    @PostMapping("/generate")
    public ApiResponse<ImageRecord> generate(@RequestBody GenerateDto dto) {
        JwtUserPrincipal principal = SecurityUtil.current();
        return ApiResponse.success(imageService.generate(principal.userId(), principal.username(), dto.getPrompt(), dto.getQualityCode()));
    }

    @GetMapping("/configs")
    public ApiResponse<java.util.List<ImageGenerationConfigDto>> configs() {
        return ApiResponse.success(configService.publicList());
    }

    @GetMapping("/history")
    public ApiResponse<Page<ImageRecord>> history(@RequestParam(defaultValue = "1") long page,
                                                   @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(imageService.history(SecurityUtil.current().userId(), page, size));
    }
}
