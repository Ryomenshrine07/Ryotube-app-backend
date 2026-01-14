package com.ryotube.application.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "playlists")
public class PlayList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private boolean isPublic = true;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany
    @JoinTable(
        name = "playlist_videos",
        joinColumns = @JoinColumn(name = "playlist_id"),
        inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private List<Video> videos = new ArrayList<>();

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "channel_id")
    private Channel channel;
    
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Transient
    public int getVideoCount() {
        return videos != null ? videos.size() : 0;
    }
    
    @Transient
    public Long getChannelId() {
        return channel != null ? channel.getId() : null;
    }
    
    @Transient
    public String getChannelName() {
        return channel != null ? channel.getChannelName() : null;
    }
}
