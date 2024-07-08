package com.ecommerce.api.balance.controller;

import java.util.Map;

record BalanceResponse(boolean success, String message, Map<String, Object> data) {
}
