package com.ecommerce.interfaces.exception.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CouponException extends DomainException {
    public CouponException(String message) {
        super(message);

    }
    public static class ControllerException extends CouponException {
        public ControllerException(String message) {
            super(message);
        }
    }

    public static class ServiceException extends CouponException {
        public ServiceException(String message) {
            super(message);
        }
    }

}
