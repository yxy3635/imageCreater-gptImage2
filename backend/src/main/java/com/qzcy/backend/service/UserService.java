package com.qzcy.backend.service;

import com.qzcy.backend.dto.AuthUser;
import com.qzcy.backend.dto.ChangePasswordDto;
import com.qzcy.backend.dto.UserProfileDto;

import java.math.BigDecimal;

public interface UserService {
    BigDecimal balance(Long userId);
    AuthUser currentUser(Long userId);
    AuthUser updateProfile(Long userId, UserProfileDto dto);
    void changePassword(Long userId, ChangePasswordDto dto);
}
