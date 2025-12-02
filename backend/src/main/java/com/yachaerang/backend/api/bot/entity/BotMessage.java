package com.yachaerang.backend.api.bot.entity;

import com.yachaerang.backend.api.common.BaseEntity;
import com.yachaerang.backend.api.member.entity.Member;
import lombok.Data;

@Data
public class BotMessage extends BaseEntity {

    private Long id;

    private Member sender;

    private String content;

    private BotSession botSession;
}
