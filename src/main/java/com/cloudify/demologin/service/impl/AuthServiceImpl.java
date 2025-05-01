package com.cloudify.demologin.service.impl;

import com.cloudify.demologin.dto.request.ForgotPasswordRequest;
import com.cloudify.demologin.dto.request.LoginRequest;
import com.cloudify.demologin.dto.request.ResetPasswordRequest;
import com.cloudify.demologin.dto.request.SignupRequest;
import com.cloudify.demologin.dto.request.VerifyOtpRequest;
import com.cloudify.demologin.dto.response.LoginResponse;
import com.cloudify.demologin.entity.Store;
import com.cloudify.demologin.entity.User;
import com.cloudify.demologin.entity.UserOTP;
import com.cloudify.demologin.repository.UserOTPRepository;
import com.cloudify.demologin.repository.UserRepository;
import com.cloudify.demologin.security.JwtService;
import com.cloudify.demologin.service.AuthService;
import com.cloudify.demologin.util.MailUtil;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.flywaydb.core.Flyway;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserOTPRepository userOTPRepository;
    private final MailUtil mailUtil;
    private final Random random = new SecureRandom();
    private final JdbcTemplate jdbcTemplate;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            UserOTPRepository userOTPRepository,
            MailUtil mailUtil,
            JdbcTemplate jdbcTemplate
            ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userOTPRepository = userOTPRepository;
        this.mailUtil = mailUtil;
        this.jdbcTemplate = jdbcTemplate;
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
        String token = jwtService.generateToken(user.getUsername(), user.getStore().getName());
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

        Store store = new Store();
        String storeName = request.getStore().getName();
        store.setName(storeName);
        store.setLocation(request.getStore().getLocation());
        user.setStore(store);

        String schemaName = storeName.trim().toLowerCase().replaceAll("\\s+", "_");
        String createSchemaQuery = "CREATE SCHEMA IF NOT EXISTS " + schemaName;
        jdbcTemplate.execute(createSchemaQuery);

        Flyway flyway = Flyway.configure()
                .dataSource(jdbcTemplate.getDataSource())
                .schemas(schemaName)
                .locations("classpath:db/migration/tenant")
                .load();

        flyway.migrate();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + request.getEmail()));

        // Generate OTP (6 digit)
        String otp = generateOTP();

        // Check if OTP already exists for the user
        userOTPRepository.findByEmail(user.getEmail()).ifPresent(userOTPRepository::delete);

        // Create new OTP entry
        UserOTP userOTP = new UserOTP();
        userOTP.setEmail(user.getEmail());
        userOTP.setOtp(otp);
        userOTP.setExpirationTime(LocalDateTime.now().plusMinutes(30)); // 30 minutes expiry
        userOTP.setVerified(false);

        userOTPRepository.save(userOTP);

        try {
            mailUtil.sendOtpEmail(user.getEmail(), otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // Verify OTP first
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest(request.getEmail(), request.getOtp());
        boolean verified = verifyOtp(verifyRequest);

        if (!verified) {
            throw new SecurityException("Invalid or expired OTP");
        }

        // Update password
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + request.getEmail()));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setLoginAttempts(0); // Reset login attempts
        userRepository.save(user);

        // Delete the used OTP
        userOTPRepository.findByEmail(request.getEmail()).ifPresent(userOTPRepository::delete);
    }

    private boolean verifyOtp(VerifyOtpRequest request) {
        UserOTP userOTP = userOTPRepository
                .findByEmailAndOtpAndIsVerifiedFalseAndExpirationTimeAfter(
                        request.getEmail(),
                        request.getOtp(),
                        LocalDateTime.now())
                .orElseThrow(() -> new SecurityException("Invalid or expired OTP"));

        // Mark OTP as verified
        userOTP.setVerified(true);
        userOTPRepository.save(userOTP);

        return true;
    }

    private String generateOTP() {
        // Generate 6-digit OTP
        int otpLength = 6;
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }
}
