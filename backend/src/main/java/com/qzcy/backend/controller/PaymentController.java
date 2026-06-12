package com.qzcy.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qzcy.backend.dto.ApiResponse;
import com.qzcy.backend.dto.RechargeDto;
import com.qzcy.backend.entity.PaymentRecord;
import com.qzcy.backend.service.PaymentService;
import com.qzcy.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/recharge")
    public ApiResponse<Map<String, Object>> recharge(@RequestBody RechargeDto dto) {
        return ApiResponse.success(paymentService.recharge(SecurityUtil.current().userId(), dto));
    }

    @GetMapping("/history")
    public ApiResponse<Page<PaymentRecord>> history(@RequestParam(defaultValue = "1") long page,
                                                     @RequestParam(defaultValue = "10") long size) {
        return ApiResponse.success(paymentService.history(SecurityUtil.current().userId(), page, size));
    }
}
