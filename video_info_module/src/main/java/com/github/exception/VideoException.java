package com.github.exception;
import com.github.common.exception.GlobalException;

public class VideoException extends GlobalException {

    public VideoException(final VideoErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getMessage());
    }

}