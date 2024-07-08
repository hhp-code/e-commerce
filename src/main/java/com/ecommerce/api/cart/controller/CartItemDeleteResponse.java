package com.ecommerce.api.cart.controller;

import java.util.Map;

record CartItemDeleteResponse(boolean success, String message, Map<String, Object> data) {
}
