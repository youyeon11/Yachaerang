package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.MonthlyPriceResponseDto;
import com.yachaerang.backend.api.product.repository.MonthlyPriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MonthlyPriceService {

    private final MonthlyPriceMapper monthlyPriceMapper;

    public List<MonthlyPriceResponseDto.PriceDto> getMonthlyPrices(
            String productCode, int startYear, int startMonth,
            int endYear, int endMonth
    ) {
        return monthlyPriceMapper.getPriceDuration(productCode, startYear, startMonth, endYear, endMonth);
    }
}
