package com.ecommerce.api.exception.domain;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class DomainException  extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
