package com.qzcy.backend.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ImageGenerationConfigUpdateDto {
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
}
