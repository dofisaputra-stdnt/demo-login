package com.cloudify.demologin.controller;

import com.cloudify.demologin.dto.request.ForgotPasswordRequest;
import com.cloudify.demologin.dto.request.LoginRequest;
import com.cloudify.demologin.dto.request.ResetPasswordRequest;
import com.cloudify.demologin.dto.request.SignupRequest;
import com.cloudify.demologin.dto.response.BaseResponse;
import com.cloudify.demologin.dto.response.LoginResponse;
import com.cloudify.demologin.service.AuthService;
import com.cloudify.demologin.util.AppConstant;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/{role}/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            @PathVariable AppConstant.AuthRole role
    ) {
        LoginResponse data = authService.login(request, role);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Login successful")
                        .data(data)
                        .build()
        );
    }

    @PostMapping("/{role}/signup")
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignupRequest request,
            @PathVariable AppConstant.AuthRole role
    ) {
        authService.signup(request, role);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Signup successful")
                        .build()
        );
    }

    @PostMapping("/{role}/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            @PathVariable AppConstant.AuthRole role
    ) {
        authService.forgotPassword(request, role);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("OTP sent to email for password reset")
                        .build()
        );
    }

    @PostMapping("/{role}/reset-password")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            @PathVariable AppConstant.AuthRole role
    ) {
        authService.resetPassword(request, role);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Password reset successful")
                        .build()
        );
    }
}
