package com.yachaerang.backend.api.favorite.controller;

import com.yachaerang.backend.api.favorite.dto.request.FavoriteRequestDto;
import com.yachaerang.backend.api.favorite.dto.response.FavoriteResponseDto;
import com.yachaerang.backend.api.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 관심사 등록하기
     */
    @PostMapping("")
    public FavoriteResponseDto.RegisterDto register(
            @RequestHeader("Authorization") String accessToken,
            @RequestBody FavoriteRequestDto.RegisterDto requestDto
            ) {
        return favoriteService.register(requestDto);
    }

    /**
     * 관심사 해제하기
     */
    @DeleteMapping("/{favoriteId}")
    public void erase(
            @RequestHeader("Authorization") String accessToken,
            @PathVariable Long favoriteId
    ) {
        favoriteService.erase(favoriteId);
    }

    /**
     * 관심사 목록 전체 조회하기
     */
    @GetMapping("")
    public List<FavoriteResponseDto.ReadDto> getAll(
            @RequestHeader("Authorization") String token
    ) {
        return favoriteService.getAllFavoriteList();
    }
}
