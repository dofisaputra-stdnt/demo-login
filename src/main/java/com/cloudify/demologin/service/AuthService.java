package com.cloudify.demologin.service;

import com.cloudify.demologin.dto.request.LoginRequest;
import com.cloudify.demologin.dto.request.SignupRequest;
import com.cloudify.demologin.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    void signup(SignupRequest request);

    void sendVerificationOtp(String email);
}
