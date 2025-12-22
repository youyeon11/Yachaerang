package com.yachaerang.backend.api.bookmark.service;

import com.yachaerang.backend.api.bookmark.dto.response.BookmarkResponseDto;
import com.yachaerang.backend.api.bookmark.entity.Bookmark;
import com.yachaerang.backend.api.bookmark.repository.BookmarkMapper;
import com.yachaerang.backend.global.auth.jwt.AuthenticatedMemberProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Book;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final AuthenticatedMemberProvider authenticatedMemberProvider;
    private final BookmarkMapper bookmarkMapper;

    /**
     * 북마크 저장하기
     */
    @Transactional
    public void register(Long articleId) {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();

        // 이미 등록된 것인지 조회

        Bookmark bookmark = Bookmark.builder()
                .articleId(articleId)
                .memberId(memberId).build();
        int result = bookmarkMapper.save(bookmark);

        if (result != 1) {

        }
        return;
    }

    /**
     * 북마크 해제하기
     */
    public void eraseBookmark(Long articleId) {
        Long memberId = authenticatedMemberProvider.getCurrentMemberId();

        // 등록된 북마크인지 조회


        // 해제
        int result = bookmarkMapper.delete(memberId, articleId);
        if (result != 1) {

        }
    }

    /**
     * 북마크 전체 조회하기
     */

}
