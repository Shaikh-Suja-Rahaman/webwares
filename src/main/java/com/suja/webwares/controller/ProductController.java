package com.suja.webwares.controller;


import com.suja.webwares.model.Product;
import com.suja.webwares.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")

public class ProductController {
    private final ProductService productService;
    ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product){
        return productService.createProduct(product);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }



}
