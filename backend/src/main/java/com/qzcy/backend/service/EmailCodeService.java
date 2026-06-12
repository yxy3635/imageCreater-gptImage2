package com.qzcy.backend.service;

import java.util.Map;

public interface EmailCodeService {
    Map<String, Object> sendCode(String email, String scene);
    void verify(String email, String scene, String code);
}
