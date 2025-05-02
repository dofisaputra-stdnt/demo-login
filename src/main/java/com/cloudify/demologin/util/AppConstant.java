package com.cloudify.demologin.util;

public class AppConstant {

    public enum AuthRole {
        ADMIN,
        CUSTOMER
    }

    public static final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";
    public static final String ACCOUNT_LOCKED = "Account locked due to too many failed login attempts";
}
