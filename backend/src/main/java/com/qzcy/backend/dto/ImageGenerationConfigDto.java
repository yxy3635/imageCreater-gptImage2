package com.qzcy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageGenerationConfigDto {
    private Long id;
    private String code;
    private String name;
    private String model;
    private String apiKeyMasked;
    private String apiBaseUrl;
    private String endpointPath;
    private String size;
    private String quality;
    private BigDecimal price;
    private Boolean enabled;
    private Integer sortOrder;
}
