package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.YearlyPriceResponseDto;
import com.yachaerang.backend.api.product.service.YearlyPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/yearly-prices")
@RestController
@RequiredArgsConstructor
public class YearlyPriceController {

    private YearlyPriceService yearlyPriceService;

    @GetMapping("/{productCode}")
    public List<YearlyPriceResponseDto.PriceDto> getPrices(
            @PathVariable("productCode") String productCode,
            @RequestParam("startYear") int startYear,
            @RequestParam("endYear") int endYear
    ) {
        return yearlyPriceService.getPrices(productCode, startYear, endYear);
    }

    public YearlyPriceResponseDto.PriceDto getPrice(
            @PathVariable("productCode") String productCode,
            @RequestParam("year") int year
    ) {
        return yearlyPriceService.getPrice(productCode, year);
    }
}
