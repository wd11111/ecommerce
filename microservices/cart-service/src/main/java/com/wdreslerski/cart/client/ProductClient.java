package com.wdreslerski.cart.client;

import com.wdreslerski.cart.common.ApiResponse;
import com.wdreslerski.cart.dto.ProductDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ApiResponse<ProductDTO> getProductById(@PathVariable("id") Long id);
}
