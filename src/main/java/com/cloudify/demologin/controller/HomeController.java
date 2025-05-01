package com.cloudify.demologin.controller;

import com.cloudify.demologin.dto.response.BaseResponse;
import com.cloudify.demologin.dto.response.HomeResponse;
import com.cloudify.demologin.service.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping
    public ResponseEntity<?> home() {
        HomeResponse data = homeService.getHome();
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Welcome to the home page")
                        .data(data)
                        .build()
        );
    }
}
