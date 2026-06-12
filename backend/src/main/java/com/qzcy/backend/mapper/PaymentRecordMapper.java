package com.qzcy.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzcy.backend.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
    @Select("SELECT COALESCE(SUM(amount), 0) FROM payment_record WHERE status = 'completed' AND type IN ('balance','third_party','wechat','alipay')")
    BigDecimal totalRevenue();
}
