package com.ryotube.application.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "short_comment_replies")
public class ShortCommentReply {
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
    @JoinColumn(name = "comment_id")
    @JsonBackReference(value = "comment-replies")
    private ShortComment comment;

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
