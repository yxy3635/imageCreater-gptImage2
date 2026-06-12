package com.qzcy.backend.dto;

import lombok.Data;

@Data
public class ForgotPasswordDto {
    private String email;
    private String code;
    private String newPassword;
}
