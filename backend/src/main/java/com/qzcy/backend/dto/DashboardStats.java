package com.qzcy.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DashboardStats {
    private Long totalUsers;
    private Long totalImages;
    private Long todayImages;
    private BigDecimal totalRevenue;
    private List<Map<String, Object>> recentRegistrations;
    private List<Map<String, Object>> generationTrend;
}
