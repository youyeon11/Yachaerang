package com.yachaerang.backend.api.product.repository;

import com.yachaerang.backend.api.product.dto.response.ProductResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    /*
    전체 ItemName과 ItemCode 반환하기
     */
    List<ProductResponseDto.ItemDto> findAllItemNameAndItemCodes();

    /*
    전체 ProductName 반환하기 (ItemCode 기반)
     */
    List<ProductResponseDto.SubItemDto> findProductNameByItemCode(
            @Param("itemCode") String itemCode
    );
}
