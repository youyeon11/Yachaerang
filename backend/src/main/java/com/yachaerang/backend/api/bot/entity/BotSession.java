package com.yachaerang.backend.api.bot.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.member.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

public class BotSession extends BaseEntity {

    private Long id;

    private LocalDateTime startedAt;

    private Member member;

    private List<BotMessage> botMessageList;
}
