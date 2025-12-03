package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.ProductResponseDto;
import com.yachaerang.backend.api.product.repository.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductMapper productMapper;

    /*
    itemName에 대하여 반환
     */
    public List<ProductResponseDto.ItemDto> getItemNames() {
        return productMapper.findAllItemNameAndItemCodes();
    }

    /*
    상위의 itemCode 기반
     */
    public List<ProductResponseDto.SubItemDto> getProductNames(String itemCode) {
        return  productMapper.findProductNameByItemCode(itemCode);
    }
}
