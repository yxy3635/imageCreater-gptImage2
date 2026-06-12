package com.qzcy.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzcy.backend.entity.ImageGenerationMetric;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ImageGenerationMetricMapper extends BaseMapper<ImageGenerationMetric> {
    @Select("SELECT COALESCE(ROUND(AVG(duration_ms)), 0) FROM image_generation_metric")
    Long averageDurationMs();
}
