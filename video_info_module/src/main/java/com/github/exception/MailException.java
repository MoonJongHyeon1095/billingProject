package com.github.exception;

import com.github.common.exception.GlobalException;

public class MailException extends GlobalException {
    public MailException(final MailErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getMessage());
    }
}
