package com.qzcy.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("image_generation_metric")
public class ImageGenerationMetric {
    private Long id;
    private Long imageRecordId;
    private Long userId;
    private String qualityCode;
    private Long durationMs;
    private LocalDateTime createdAt;
}
