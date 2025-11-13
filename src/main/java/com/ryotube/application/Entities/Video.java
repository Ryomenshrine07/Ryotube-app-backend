package com.ryotube.application.Entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "videos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long likes;
    private long dislikes;
    private long views;
    private String tile;
    private String duration;
    private LocalDateTime uploadDateTime;
    private String cloudId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String videoChannelName;
    private String videoCategory;
    private String videoThumbnail;
    private String videoUrl;

    @OneToMany(mappedBy = "video",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "channel_id")
    Channel channel;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "playlist_id")
    private PlayList playlist;

    @Transient
    public Long getChannelId(){
        return channel != null ? channel.getId() : null;
    }
    @Transient
    public String getChannelName(){
        return channel != null ? channel.getChannelName() : null;
    }
    @Transient
    public Long getChannelSubscriberCount(){
        return channel != null ? channel.getSubscribersCount() : null;
    }
    @PrePersist
    public void onCreate() {
        this.uploadDateTime = LocalDateTime.now();
    }

}
