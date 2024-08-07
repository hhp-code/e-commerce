package com.ecommerce.interfaces.exception.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProductException extends DomainException {
    public ProductException(String message) {
        super(message);

    }

    public static class ServiceException extends ProductException {
        public ServiceException(String message) {
            super(message);
        }
    }
}
