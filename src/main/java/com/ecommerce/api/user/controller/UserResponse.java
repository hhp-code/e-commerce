package com.ecommerce.api.user.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;

record UserResponse(Long id, String username, BigDecimal balance, Boolean isDeleted, LocalDateTime createdAt) {
}
