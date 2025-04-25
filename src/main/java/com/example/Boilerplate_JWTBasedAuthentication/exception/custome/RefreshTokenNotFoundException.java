package com.example.Boilerplate_JWTBasedAuthentication.exception.custome;

public class RefreshTokenNotFoundException extends Exception{
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
