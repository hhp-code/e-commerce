package com.ecommerce.api.cart.controller;

import java.util.Map;

record CartItemUpdateResponse(boolean success, String message, Map<String, Object> data) {
}
