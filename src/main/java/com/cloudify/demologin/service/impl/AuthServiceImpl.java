package com.cloudify.demologin.service.impl;

import com.cloudify.demologin.dto.request.LoginRequest;
import com.cloudify.demologin.dto.request.SignupRequest;
import com.cloudify.demologin.dto.response.LoginResponse;
import com.cloudify.demologin.entity.User;
import com.cloudify.demologin.repository.UserRepository;
import com.cloudify.demologin.security.JwtService;
import com.cloudify.demologin.service.AuthService;
import jakarta.persistence.EntityExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        final String errorMessage = "Invalid username or password";
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new SecurityException(errorMessage));

        // Check if the user is locked out
        if (user.getLoginAttempts() >= 3) {
            throw new SecurityException("Account locked due to too many failed login attempts");
        }

        // Authenticate the user
        Authentication authenticationRequest;
        Authentication authenticationResponse;
        try {
            authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(request.getUsername(), request.getPassword());
            authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
        } catch (Exception e) {
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            userRepository.save(user);
            throw new SecurityException(errorMessage);
        }

        // Reset login attempts on successful authentication
        if (user.getLoginAttempts() > 0) {
            user.setLoginAttempts(0);
            userRepository.save(user);
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        String token = jwtService.generateToken(request.getUsername());
        return new LoginResponse(token);
    }

    @Override
    public void signup(SignupRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new EntityExistsException("Username already used");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void sendVerificationOtp(String email) {

    }
}
