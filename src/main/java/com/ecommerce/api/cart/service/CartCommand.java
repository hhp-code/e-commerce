package com.ecommerce.api.cart.service;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CartCommand {
    public record Update(long userId, long productId, int quantity){}

    public record Add(long userId, long productId, int quantity){}
}
