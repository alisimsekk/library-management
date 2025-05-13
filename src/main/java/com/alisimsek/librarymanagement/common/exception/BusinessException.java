package com.alisimsek.librarymanagement.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends BaseException {

    public BusinessException(String message) {

        super(null, HttpStatus.BAD_REQUEST, "4003", new Object[]{message});
    }
} 