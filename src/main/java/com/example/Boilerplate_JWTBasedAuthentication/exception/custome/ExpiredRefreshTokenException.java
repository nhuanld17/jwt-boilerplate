package com.example.Boilerplate_JWTBasedAuthentication.exception.custome;

public class ExpiredRefreshTokenException extends Exception{
    public ExpiredRefreshTokenException(String message) {
        super(message);
    }
}
