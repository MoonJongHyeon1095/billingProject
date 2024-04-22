package com.github.exception;
import com.github.common.exception.GlobalException;

public class AdException extends GlobalException {

    public AdException(final AdErrorCode errorCode) {
        super(errorCode.getStatus(), errorCode.getMessage());
    }

    public static class InsertWatchHistoryException extends AdException {
        public InsertWatchHistoryException(final AdErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class InvalidEmailException extends AdException {
        public InvalidEmailException(final AdErrorCode errorCode){
            super(errorCode);}
    }

    public static class UserDuplicatedException extends AdException {
        public UserDuplicatedException(final AdErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class VideoNotFoundException extends AdException {
        public VideoNotFoundException(final AdErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class ProfileNotFoundException extends AdException {
        public ProfileNotFoundException(final AdErrorCode errorCode) {
            super(errorCode);
        }
    }

    public static class UserUnauthorizedException extends AdException {
        public UserUnauthorizedException(final AdErrorCode errorCode) {
            super(errorCode);
        }
    }
}