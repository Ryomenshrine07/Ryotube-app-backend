package com.ryotube.application.DTOs;

import com.ryotube.application.Entities.PlayList;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayListDTO {
    private Long id;
    private String name;
    private String description;
    private boolean isPublic;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int videoCount;
    private Long channelId;
    private String channelName;
    private List<VideoSummaryDTO> videos;

    public static PlayListDTO fromEntity(PlayList playlist) {
        PlayListDTO dto = new PlayListDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setDescription(playlist.getDescription());
        dto.setPublic(playlist.isPublic());
        dto.setCreatedAt(playlist.getCreatedAt());
        dto.setUpdatedAt(playlist.getUpdatedAt());
        dto.setVideoCount(playlist.getVideoCount());
        dto.setChannelId(playlist.getChannelId());
        dto.setChannelName(playlist.getChannelName());
        
        if (playlist.getVideos() != null) {
            dto.setVideos(playlist.getVideos().stream()
                .map(VideoSummaryDTO::fromEntity)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public static PlayListDTO fromEntityWithoutVideos(PlayList playlist) {
        PlayListDTO dto = new PlayListDTO();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setDescription(playlist.getDescription());
        dto.setPublic(playlist.isPublic());
        dto.setCreatedAt(playlist.getCreatedAt());
        dto.setUpdatedAt(playlist.getUpdatedAt());
        dto.setVideoCount(playlist.getVideoCount());
        dto.setChannelId(playlist.getChannelId());
        dto.setChannelName(playlist.getChannelName());
        return dto;
    }
}