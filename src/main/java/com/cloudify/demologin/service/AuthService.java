package com.cloudify.demologin.service;

import com.cloudify.demologin.dto.request.ForgotPasswordRequest;
import com.cloudify.demologin.dto.request.LoginRequest;
import com.cloudify.demologin.dto.request.ResetPasswordRequest;
import com.cloudify.demologin.dto.request.SignupRequest;
import com.cloudify.demologin.dto.request.VerifyOtpRequest;
import com.cloudify.demologin.dto.response.LoginResponse;
import com.cloudify.demologin.util.AppConstant;

public interface AuthService {
    LoginResponse login(LoginRequest request, AppConstant.AuthRole role);

    void signup(SignupRequest request, AppConstant.AuthRole role);

    void forgotPassword(ForgotPasswordRequest request, AppConstant.AuthRole role);

    void resetPassword(ResetPasswordRequest request, AppConstant.AuthRole role);
}
