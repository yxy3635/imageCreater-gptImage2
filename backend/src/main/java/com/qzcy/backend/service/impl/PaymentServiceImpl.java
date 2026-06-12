package com.qzcy.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzcy.backend.dto.RechargeDto;
import com.qzcy.backend.entity.PaymentRecord;
import com.qzcy.backend.exception.BusinessException;
import com.qzcy.backend.mapper.PaymentRecordMapper;
import com.qzcy.backend.mapper.UserMapper;
import com.qzcy.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final UserMapper userMapper;
    private final PaymentRecordMapper paymentRecordMapper;

    @Override
    @Transactional
    public void deductBalance(Long userId, BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "扣费金额无效");
        }
        int updated = userMapper.deductBalance(userId, amount);
        if (updated == 0) {
            throw new BusinessException(402, "余额不足，请先充值");
        }
        PaymentRecord record = new PaymentRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setType("balance");
        record.setStatus("completed");
        record.setCreatedAt(LocalDateTime.now());
        paymentRecordMapper.insert(record);
    }

    @Override
    @Transactional
    public Map<String, Object> recharge(Long userId, RechargeDto dto) {
        BigDecimal amount = dto.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "充值金额必须大于0");
        }
        String type = dto.getType() == null ? "balance" : dto.getType();
        PaymentRecord record = new PaymentRecord();
        record.setUserId(userId);
        record.setAmount(amount);
        record.setType(type);
        record.setCreatedAt(LocalDateTime.now());

        if ("balance".equals(type)) {
            userMapper.addBalance(userId, amount);
            record.setStatus("completed");
            paymentRecordMapper.insert(record);
            return Map.of("message", "模拟充值成功", "amount", amount);
        }

        record.setStatus("pending");
        paymentRecordMapper.insert(record);
        return Map.of("message", "该支付方式暂未开放", "type", type);
    }

    @Override
    public Page<PaymentRecord> history(Long userId, long page, long size) {
        return paymentRecordMapper.selectPage(
                Page.of(page, size),
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getUserId, userId)
                        .orderByDesc(PaymentRecord::getCreatedAt)
        );
    }
}
