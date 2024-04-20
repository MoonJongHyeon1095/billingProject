package com.github.exception;
import com.github.common.exception.GlobalException;

public class VideoException extends GlobalException {

    public VideoException(final VideoErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getMessage());
    }

    public static class InvalidPasswordException extends VideoException {
        public InvalidPasswordException(final VideoErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class InvalidEmailException extends VideoException {
        public InvalidEmailException(final VideoErrorCode errorCode){
            super(errorCode);}
    }

    public static class UserDuplicatedException extends VideoException {
        public UserDuplicatedException(final VideoErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class VideoNotFoundException extends VideoException {
        public VideoNotFoundException(final VideoErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class ProfileNotFoundException extends VideoException {
        public ProfileNotFoundException(final VideoErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class UserUnauthorizedException extends VideoException {
        public UserUnauthorizedException(final VideoErrorCode errorCode) {
            super(errorCode);
        }
    }
}