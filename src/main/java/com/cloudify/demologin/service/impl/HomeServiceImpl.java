package com.cloudify.demologin.service.impl;

import com.cloudify.demologin.dto.response.HomeResponse;
import com.cloudify.demologin.entity.User;
import com.cloudify.demologin.repository.UserRepository;
import com.cloudify.demologin.service.HomeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class HomeServiceImpl implements HomeService {

    private final UserRepository userRepository;

    public HomeServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public HomeResponse getHome() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User no longer exists"));

        return HomeResponse.builder()
                .id(user.getId().toString())
                .name(user.getUsername())
                .email(user.getEmail())
                .loginAttempts(user.getLoginAttempts())
                .build();
    }
}
