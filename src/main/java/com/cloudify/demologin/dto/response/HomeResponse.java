package com.cloudify.demologin.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HomeResponse {
    private String id;
    private String name;
    private String email;
    private int loginAttempts;
}
