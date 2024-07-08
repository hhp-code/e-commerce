package com.ecommerce.api.order.controller;

import java.util.List;

record OrderRequest(long customerId, List<OrderItem> items) {
}
