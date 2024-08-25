package com.ecommerce.application.saga;

public enum OrderState {
    CREATED,
    STOCK_DEDUCTED,
    POINT_DEDUCTED,
    COMPLETED,
    CANCELLED
}