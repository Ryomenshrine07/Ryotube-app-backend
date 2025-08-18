package com.ryotube.application.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String userPicURL;
    private long userChannelId;
    private String commentData;
    private LocalDateTime timestamp;
    private long likes;
    private long disLikes;

    @ManyToOne
    @JoinColumn(name = "video_id")
    @JsonBackReference
    private Video video;

    @JsonManagedReference
    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CommentReply> replies;

    @PrePersist
    public void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
