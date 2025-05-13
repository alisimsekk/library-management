package com.alisimsek.librarymanagement.common.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends BaseException {

    public AccessDeniedException() {
        super(HttpStatus.FORBIDDEN, "4002");
    }
}
