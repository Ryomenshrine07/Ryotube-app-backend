package com.ryotube.application.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shorts", indexes = {
    @Index(name = "idx_short_feed", columnList = "feedScore DESC, uploadDateTime DESC, id DESC")
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Short {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private long likes;
    private long dislikes;
    private long views;
    private String title;
    private String duration;
    private LocalDateTime uploadDateTime;
    private String cloudId;
    
    @Column(columnDefinition = "DOUBLE DEFAULT 0.0")
    private Double feedScore = 0.0;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String shortChannelName;
    private String shortCategory;
    private String shortThumbnail;
    private String shortUrl;

    @ManyToOne
    @JsonBackReference(value = "channel-shorts")
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @OneToMany(mappedBy = "shortVideo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "short-comments")
    private List<ShortComment> comments = new ArrayList<>();

    @Transient
    public Long getChannelId() {
        return channel != null ? channel.getId() : null;
    }

    @Transient
    public String getChannelName() {
        return channel != null ? channel.getChannelName() : null;
    }

    @Transient
    public String getChannelPicUrl() {
        return channel != null ? channel.getChannelPicURL() : null;
    }

    @PrePersist
    public void onCreate() {
        this.uploadDateTime = LocalDateTime.now();
    }
}
