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
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "short_comments")
public class ShortComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String username;
    private String userPicURL;
    private long userChannelId;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String commentData;
    
    private LocalDateTime timestamp;
    private long likes;
    private long disLikes;

    @ManyToOne
    @JoinColumn(name = "short_id")
    @JsonBackReference(value = "short-comments")
    private Short shortVideo;

    @JsonManagedReference(value = "comment-replies")
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShortCommentReply> replies = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
