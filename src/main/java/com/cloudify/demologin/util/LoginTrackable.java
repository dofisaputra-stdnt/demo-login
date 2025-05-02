package com.cloudify.demologin.util;

public interface LoginTrackable {
    String getUsername();

    int getLoginAttempts();

    void setLoginAttempts(int attempts);
}

