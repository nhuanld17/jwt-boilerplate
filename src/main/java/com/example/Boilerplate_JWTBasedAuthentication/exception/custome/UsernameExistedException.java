package com.example.Boilerplate_JWTBasedAuthentication.exception.custome;

public class UsernameExistedException extends Exception{
    public UsernameExistedException(String message) {
        super(message);
    }
}
