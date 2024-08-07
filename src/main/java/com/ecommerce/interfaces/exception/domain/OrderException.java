package com.ecommerce.interfaces.exception.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderException extends DomainException {
    public OrderException(String message) {
        super(message);
    }
    public static class ControllerException extends OrderException {
        public ControllerException(String message) {
            super(message);
        }
    }

    public static class ServiceException extends OrderException {
        public ServiceException(String message) {
            super(message);
        }
    }
}
