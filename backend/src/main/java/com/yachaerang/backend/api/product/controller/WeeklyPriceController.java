package com.yachaerang.backend.api.product.controller;

import com.yachaerang.backend.api.product.dto.response.WeeklyPriceResponseDto;
import com.yachaerang.backend.api.product.service.WeeklyPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/api/v1/weekly-prices")
@RequiredArgsConstructor
@RestController
public class WeeklyPriceController {

    private final WeeklyPriceService weeklyPriceService;

    @GetMapping("/{productCode}")
    public List<WeeklyPriceResponseDto.PriceRecordDto> getWeeklyPrice(
            @PathVariable("productCode") String productCode,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
            ) {
        return weeklyPriceService.getPriceDuration(productCode, startDate, endDate);
    }
}
