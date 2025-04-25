package com.example.Boilerplate_JWTBasedAuthentication.exception.custome;

public class InvalidRefreshTokenException extends Exception{
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
