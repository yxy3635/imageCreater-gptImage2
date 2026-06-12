package com.qzcy.backend.service;

import com.qzcy.backend.dto.AuthUser;
import com.qzcy.backend.dto.ForgotPasswordDto;
import com.qzcy.backend.dto.LoginDto;
import com.qzcy.backend.dto.LoginResponse;
import com.qzcy.backend.dto.RegisterDto;

public interface AuthService {
    AuthUser register(RegisterDto dto);
    LoginResponse login(LoginDto dto);
    void resetPassword(ForgotPasswordDto dto);
}
