package com.qzcy.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminStatsMapper {
    @Select("SELECT DATE(created_at) AS date, COUNT(*) AS count FROM `user` WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 6 DAY) GROUP BY DATE(created_at) ORDER BY date")
    List<Map<String, Object>> recentRegistrations();
}
