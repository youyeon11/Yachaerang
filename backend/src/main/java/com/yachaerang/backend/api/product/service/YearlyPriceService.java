package com.yachaerang.backend.api.product.service;

import com.yachaerang.backend.api.product.dto.response.YearlyPriceResponseDto;
import com.yachaerang.backend.api.product.repository.YearlyPriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YearlyPriceService {

    private YearlyPriceMapper yearlyPriceMapper;

    public List<YearlyPriceResponseDto.PriceDto> getPrices(
            String productCode, int startYear, int endYear
    ) {
        return yearlyPriceMapper.getPriceDuration(productCode, startYear, endYear);
    }

    public YearlyPriceResponseDto.PriceDto getPrice(
            String productCode, int targetYear
    ) {
        return yearlyPriceMapper.getPriceSpecification(productCode, targetYear);
    }
}
