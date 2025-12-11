package com.yachaerang.backend.api.farm.controller;

import com.yachaerang.backend.api.farm.dto.response.FarmResponseDto;
import com.yachaerang.backend.api.farm.dto.resquest.FarmRequestDto;
import com.yachaerang.backend.api.farm.service.FarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RequestMapping("/api/v1/farms")
@RestController
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    @PostMapping("")
    public CompletableFuture<FarmResponseDto.InfoDto> save(
            @RequestHeader("Authorization") String token,
            @RequestBody FarmRequestDto.InfoDto requestDto
    ) {
        return farmService.saveFarmInfo(requestDto);
    }

    @GetMapping("")
    public FarmResponseDto .InfoDto get(@RequestHeader("Authorization") String token) {
        return farmService.getFarm();
    }

    @PatchMapping("")
    public CompletableFuture<FarmResponseDto.InfoDto> update(
            @RequestHeader("Authorization") String token,
            @RequestBody FarmRequestDto.InfoDto requestDto
    ) {
        return farmService.updateFarmInfo(requestDto);
    }
}
