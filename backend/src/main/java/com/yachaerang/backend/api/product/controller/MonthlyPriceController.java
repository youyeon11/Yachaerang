package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.MonthlyPriceResponseDto;
import com.yachaerang.backend.api.product.service.MonthlyPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/monthly-prices")
@RestController
@RequiredArgsConstructor
public class MonthlyPriceController {

    private final MonthlyPriceService monthlyPriceService;

    @GetMapping("/{productCode}")
    public List<MonthlyPriceResponseDto.PriceDto> getMonthlyPrices(
            @PathVariable("productCode") String productCode,
            @RequestParam("startYear") int startYear, @RequestParam("startMonth") int startMonth,
            @RequestParam("endYear") int endYear, @RequestParam("endMonth") int endMonth
    ) {
        return monthlyPriceService.getMonthlyPrices(productCode, startYear, startMonth, endYear, endMonth);
    }
}
