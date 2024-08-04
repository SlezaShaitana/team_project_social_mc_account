package com.social.mc_account.exception;

import lombok.Data;

@Data
public class AppError {
    private int statusCode;
    private String message;
}