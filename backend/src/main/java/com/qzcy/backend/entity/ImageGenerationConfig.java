package com.qzcy.backend.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("image_generation_config")
public class ImageGenerationConfig {
    private Long id;
    private String code;
    private String name;
    private String model;
    private String apiKey;
    private String apiBaseUrl;
    private String endpointPath;
    private String size;
    private String quality;
    private BigDecimal price;
    private Boolean enabled;
    private Integer sortOrder;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
