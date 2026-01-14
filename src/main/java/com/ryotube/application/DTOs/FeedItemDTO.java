package com.ryotube.application.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedItemDTO {
    private Long id;
    private String type; // "VIDEO" or "SHORT"
    private String title;
    private String description;
    private String thumbnail;
    private String url;
    private String duration;
    private Long views;
    private Long likes;
    private Long dislikes;
    private Double feedScore;
    private LocalDateTime createdAt;
    private Long channelId;
    private String channelName;
    private String channelPicUrl;
    private Long subscriberCount;
    private int commentCount;
}
