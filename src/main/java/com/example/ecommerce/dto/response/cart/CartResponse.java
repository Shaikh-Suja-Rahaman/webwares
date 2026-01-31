package com.example.ecommerce.dto.response.cart;

import java.util.List;

public class CartResponse {
    private String id;
    private List<CartItemResponse> items;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }
}

