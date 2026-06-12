package com.qzcy.backend.controller;

import com.qzcy.backend.dto.ApiResponse;
import com.qzcy.backend.dto.AuthUser;
import com.qzcy.backend.dto.ChangePasswordDto;
import com.qzcy.backend.dto.UserProfileDto;
import com.qzcy.backend.service.UserService;
import com.qzcy.backend.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/balance")
    public ApiResponse<BigDecimal> balance() {
        return ApiResponse.success(userService.balance(SecurityUtil.current().userId()));
    }

    @GetMapping("/me")
    public ApiResponse<AuthUser> me() {
        return ApiResponse.success(userService.currentUser(SecurityUtil.current().userId()));
    }

    @PutMapping("/profile")
    public ApiResponse<AuthUser> updateProfile(@RequestBody UserProfileDto dto) {
        return ApiResponse.success(userService.updateProfile(SecurityUtil.current().userId(), dto));
    }

    @PostMapping("/password")
    public ApiResponse<Void> changePassword(@RequestBody ChangePasswordDto dto) {
        userService.changePassword(SecurityUtil.current().userId(), dto);
        return ApiResponse.success(null);
    }
}
