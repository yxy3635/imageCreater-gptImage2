package com.qzcy.backend.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("image_record")
public class ImageRecord {
    private Long id;
    private Long userId;
    private String prompt;
    private String generatedImageUrl;
    private String status;
    private BigDecimal cost;
    private LocalDateTime createdAt;
}
