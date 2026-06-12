package com.qzcy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageEstimateDto {
    private Long averageDurationMs;
    private Integer sampleCount;
}
