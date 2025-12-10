package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.ProductResponseDto;
import com.yachaerang.backend.api.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    /*
    상품의 품목 부분 조회하기(상위)
     */
    @GetMapping("/item")
    public List<ProductResponseDto.ItemDto> getItems() {
        return productService.getItemNames();
    }

    /*
    상품의 하위부류 조회하기(kindName + productRank)
     */
    @GetMapping("/{itemCode}/subItem")
    public List<ProductResponseDto.SubItemDto> getProducts(@PathVariable(name="itemCode") String itemCode) {
        return productService.getProductNames(itemCode);
    }
}
