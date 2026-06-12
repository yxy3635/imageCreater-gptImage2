package com.qzcy.backend.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qzcy.backend.entity.ImageGenerationConfig;
import com.qzcy.backend.mapper.ImageGenerationConfigMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class ImageGenerationConfigInitializer implements InitializingBean {
    private final ImageGenerationConfigMapper mapper;

    @Value("${spring.ai.openai.api-key:}")
    private String defaultApiKey;

    @Override
    public void afterPropertiesSet() {
        try {
            createIfAbsent("1k", "1K 标准图像", "1024x1024", "standard", new BigDecimal("0.10"), 10);
            createIfAbsent("2k", "2K 高清图像", "1792x1024", "hd", new BigDecimal("0.20"), 20);
            createIfAbsent("4k", "4K 超清图像", "1792x1024", "hd", new BigDecimal("0.40"), 30);
        } catch (DataAccessException ex) {
            log.warn("image_generation_config表尚未创建，请执行db/upgrade-image-config.sql后再使用生图配置功能");
        }
    }

    private void createIfAbsent(String code, String name, String size, String quality, BigDecimal price, Integer sortOrder) {
        Long count = mapper.selectCount(new LambdaQueryWrapper<ImageGenerationConfig>().eq(ImageGenerationConfig::getCode, code));
        if (count > 0) {
            return;
        }
        ImageGenerationConfig config = new ImageGenerationConfig();
        config.setCode(code);
        config.setName(name);
        config.setModel("dall-e-3");
        config.setApiKey(defaultApiKey == null || defaultApiKey.isBlank() || "replace-me".equals(defaultApiKey) ? null : defaultApiKey);
        config.setApiBaseUrl("https://api.openai.com");
        config.setEndpointPath("/v1/images/generations");
        config.setSize(size);
        config.setQuality(quality);
        config.setPrice(price);
        config.setEnabled(true);
        config.setSortOrder(sortOrder);
        mapper.insert(config);
    }
}
