package com.qzcy.backend.service;

import com.qzcy.backend.dto.ImageGenerationConfigDto;
import com.qzcy.backend.dto.ImageGenerationConfigUpdateDto;
import com.qzcy.backend.entity.ImageGenerationConfig;

import java.util.List;

public interface ImageGenerationConfigService {
    List<ImageGenerationConfigDto> adminList();
    List<ImageGenerationConfigDto> publicList();
    ImageGenerationConfigDto update(Long id, ImageGenerationConfigUpdateDto dto);
    ImageGenerationConfig requireEnabled(String code);
}
