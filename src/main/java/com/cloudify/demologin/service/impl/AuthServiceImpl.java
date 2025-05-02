package com.cloudify.demologin.service.impl;

import com.cloudify.demologin.dto.request.ForgotPasswordRequest;
import com.cloudify.demologin.dto.request.LoginRequest;
import com.cloudify.demologin.dto.request.ResetPasswordRequest;
import com.cloudify.demologin.dto.request.SignupRequest;
import com.cloudify.demologin.dto.request.VerifyOtpRequest;
import com.cloudify.demologin.dto.response.LoginResponse;
import com.cloudify.demologin.entity.Customer;
import com.cloudify.demologin.entity.Store;
import com.cloudify.demologin.entity.User;
import com.cloudify.demologin.entity.UserOTP;
import com.cloudify.demologin.repository.CustomerRepository;
import com.cloudify.demologin.repository.StoreRepository;
import com.cloudify.demologin.repository.UserOTPRepository;
import com.cloudify.demologin.repository.UserRepository;
import com.cloudify.demologin.security.JwtService;
import com.cloudify.demologin.service.AuthService;
import com.cloudify.demologin.util.AppConstant;
import com.cloudify.demologin.util.LoginTrackable;
import com.cloudify.demologin.util.MailUtil;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;
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
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserOTPRepository userOTPRepository;
    private final MailUtil mailUtil;
    private final Random random = new SecureRandom();
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            UserOTPRepository userOTPRepository,
            MailUtil mailUtil,
            StoreRepository storeRepository,
            CustomerRepository customerRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userOTPRepository = userOTPRepository;
        this.mailUtil = mailUtil;
        this.storeRepository = storeRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public LoginResponse login(LoginRequest request, AppConstant.AuthRole role) {
        Authentication authenticationResponse;
        String username = request.getUsername();
        String password = request.getPassword();

        Store store;

        if (role == AppConstant.AuthRole.ADMIN) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new SecurityException(AppConstant.INVALID_USERNAME_OR_PASSWORD));

            authenticationResponse = authenticateAndTrackAttempts(user, username, password, userRepository);
            store = user.getStore();
        } else {
            Customer customer = customerRepository.findByUsername(username)
                    .orElseThrow(() -> new SecurityException(AppConstant.INVALID_USERNAME_OR_PASSWORD));

            authenticationResponse = authenticateAndTrackAttempts(customer, username, password, customerRepository);
            store = customer.getStore();
        }

        SecurityContextHolder.getContext().setAuthentication(authenticationResponse);
        String token = jwtService.generateToken(username, role == AppConstant.AuthRole.ADMIN ? "public" : store.getName());
        return new LoginResponse(token);
    }

    private <T extends LoginTrackable> Authentication authenticateAndTrackAttempts(
            T entity, String username, String password, JpaRepository<T, UUID> repository) {

        if (entity.getLoginAttempts() >= 3) {
            throw new SecurityException(AppConstant.ACCOUNT_LOCKED);
        }

        Authentication authenticationRequest;
        Authentication authenticationResponse;
        try {
            authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
            authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);
        } catch (Exception e) {
            entity.setLoginAttempts(entity.getLoginAttempts() + 1);
            repository.save(entity);
            throw new SecurityException(AppConstant.INVALID_USERNAME_OR_PASSWORD);

        }

        if (entity.getLoginAttempts() > 0) {
            entity.setLoginAttempts(0);
            repository.save(entity);
        }
        return authenticationResponse;
    }


    @Override
    public void signup(SignupRequest request, AppConstant.AuthRole role) {
        if (role == AppConstant.AuthRole.ADMIN) {
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new EntityExistsException("Username already used");
            }

            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setStore(null);
            userRepository.save(user);
        } else {
            if (customerRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new EntityExistsException("Username already used");
            }

            Store store = storeRepository.findById(request.getStoreId())
                    .orElseThrow(() -> new EntityNotFoundException("Store not found with ID: " + request.getStoreId()));

            Customer customer = new Customer();
            customer.setUsername(request.getUsername());
            customer.setEmail(request.getEmail());
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
            customer.setStore(store);
            customerRepository.save(customer);
        }
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request, AppConstant.AuthRole role) {
        String email = request.getEmail();
        if (role == AppConstant.AuthRole.ADMIN) {
            userRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        } else {
            customerRepository.findByEmail(email)
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found with email: " + email));
        }

        // Generate OTP (6 digit)
        String otp = generateOTP();

        // Check if OTP already exists for the user
        userOTPRepository.findByEmail(email).ifPresent(userOTPRepository::delete);

        // Create new OTP entry
        UserOTP userOTP = new UserOTP();
        userOTP.setEmail(email);
        userOTP.setOtp(otp);
        userOTP.setExpirationTime(LocalDateTime.now().plusMinutes(30)); // 30 minutes expiry
        userOTP.setVerified(false);

        userOTPRepository.save(userOTP);

        try {
            mailUtil.sendOtpEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request, AppConstant.AuthRole role) {
        // Verify OTP first
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest(request.getEmail(), request.getOtp());
        boolean verified = verifyOtp(verifyRequest);

        if (!verified) {
            throw new SecurityException("Invalid or expired OTP");
        }

        if (role == AppConstant.AuthRole.ADMIN) {
            // Update password
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + request.getEmail()));

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            user.setLoginAttempts(0); // Reset login attempts
            userRepository.save(user);
        } else {
            // Update password
            Customer customer = customerRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("Customer not found with email: " + request.getEmail()));

            customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
            customer.setLoginAttempts(0); // Reset login attempts
            customerRepository.save(customer);
        }

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
