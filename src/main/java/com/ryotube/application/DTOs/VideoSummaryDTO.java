package com.ryotube.application.DTOs;

import com.ryotube.application.Entities.Video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoSummaryDTO {
    private Long id;
    private String title;
    private String duration;
    private String videoThumbnail;
    private String videoUrl;
    private long views;
    private long likes;
    private long dislikes;
    private LocalDateTime uploadDateTime;
    private Long channelId;
    private String channelName;

    public static VideoSummaryDTO fromEntity(Video video) {
        VideoSummaryDTO dto = new VideoSummaryDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTile()); // Note: Your entity has 'tile' instead of 'title'
        dto.setDuration(video.getDuration());
        dto.setVideoThumbnail(video.getVideoThumbnail());
        dto.setVideoUrl(video.getVideoUrl());
        dto.setViews(video.getViews());
        dto.setLikes(video.getLikes());
        dto.setDislikes(video.getDislikes());
        dto.setUploadDateTime(video.getUploadDateTime());
        dto.setChannelId(video.getChannelId());
        dto.setChannelName(video.getChannelName());
        return dto;
    }
}