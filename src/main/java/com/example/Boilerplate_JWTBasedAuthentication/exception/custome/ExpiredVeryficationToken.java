package com.example.Boilerplate_JWTBasedAuthentication.exception.custome;

public class ExpiredVeryficationToken extends Exception{
    public ExpiredVeryficationToken(String message) {
        super(message);
    }
}
