package com.ecommerce.interfaces.exception.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserException extends DomainException {
    public UserException(String message) {
        super(message);
    }

    public static class ServiceException extends UserException {
        public ServiceException(String message) {
            super(message);
        }
    }
}
