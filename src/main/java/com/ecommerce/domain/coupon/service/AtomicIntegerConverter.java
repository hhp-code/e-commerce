package com.ecommerce.domain.coupon.service;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.concurrent.atomic.AtomicInteger;
@Converter(autoApply = true)
public class AtomicIntegerConverter implements AttributeConverter<AtomicInteger, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AtomicInteger atomicInteger) {
        return atomicInteger == null ? null : atomicInteger.get();
    }

    @Override
    public AtomicInteger convertToEntityAttribute(Integer integer) {
        return integer == null ? null : new AtomicInteger(integer);
    }
}
