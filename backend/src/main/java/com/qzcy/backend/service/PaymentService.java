package com.qzcy.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzcy.backend.dto.RechargeDto;
import com.qzcy.backend.entity.PaymentRecord;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentService {
    void deductBalance(Long userId, BigDecimal amount);
    Map<String, Object> recharge(Long userId, RechargeDto dto);
    Page<PaymentRecord> history(Long userId, long page, long size);
}
