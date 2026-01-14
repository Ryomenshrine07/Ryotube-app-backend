package com.ryotube.application.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoSearchDTO {
    private Long id;
    private String title;
    private String description;
    private String thumbnail;
    private String duration;
    private Long views;
    private Long likes;
    private LocalDateTime uploadDateTime;
    private Long channelId;
    private String channelName;
    private String channelPicUrl;
}
