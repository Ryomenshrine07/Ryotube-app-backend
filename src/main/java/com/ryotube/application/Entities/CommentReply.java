package com.ryotube.application.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CommentReply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String commentData;
    private String userPicURL;
    private long userChannelId;
    private LocalDateTime timestamp;
    private long likes;
    private long disLikes;
    @ManyToOne
    @JoinColumn(name = "comment_id")
    @JsonBackReference
    private Comment comment;

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
