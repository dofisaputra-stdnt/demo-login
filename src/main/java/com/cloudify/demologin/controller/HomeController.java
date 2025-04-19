package com.cloudify.demologin.controller;

import com.cloudify.demologin.dto.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    @GetMapping()
    public ResponseEntity<?> home() {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Welcome to the home page " + userDetails.getUsername())
                        .build()
        );
    }
}
