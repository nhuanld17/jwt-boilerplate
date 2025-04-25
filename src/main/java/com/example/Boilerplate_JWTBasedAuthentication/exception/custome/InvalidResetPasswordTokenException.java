package com.example.Boilerplate_JWTBasedAuthentication.exception.custome;

// trường hợp token reset password bị hết hạn
public class InvalidResetPasswordTokenException extends Exception{
    public InvalidResetPasswordTokenException(String message) {
        super(message);
    }
}
