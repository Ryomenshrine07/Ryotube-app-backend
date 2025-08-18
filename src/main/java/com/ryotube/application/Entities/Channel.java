package com.ryotube.application.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ryotube.application.Profile.Banner;
import com.ryotube.application.Profile.ChannelStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String channelName;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String channelDescription;

    private String gitHubLink;
    private String twitterLink;
    private String websiteLink;
    private Long subscribersCount;
    private String channelPicURL;

    @ElementCollection
    @CollectionTable(name = "watch_later_videos", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "video_id")
    private Set<Long> watchLaterVideos = new HashSet<>();


    @ElementCollection
    @CollectionTable(name = "channel_liked_videos", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "video_id")
    private Set<Long> likedVideos = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "channel_disliked_videos", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "video_id")
    private Set<Long> dislikedVideos = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "channel_liked_comments", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "comment_id")
    private Set<Long> likedComments = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "channel_disliked_comments", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "comment_id")
    private Set<Long> dislikedComments = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "channel_liked_comments_replies", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "comment_reply_id")
    private Set<Long> likedCommentReplies = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "channel_disliked_comments_replies", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "comment_reply_id")
    private Set<Long> dislikedCommentReplies = new HashSet<>();

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "banner_id",referencedColumnName = "id")
    private Banner banner;

    @ElementCollection
    @CollectionTable(name = "subscribed_channels", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "subscribed_channel_id")
    private Set<Long> subscribedChannels = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "channel_history", joinColumns = @JoinColumn(name = "channel_id"))
    @Column(name = "video_id")
    private Set<Long> channelHistory = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "channel",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Video> videos;

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "status_id",referencedColumnName = "id")
    private ChannelStatus channelStatus;

    @JsonManagedReference
    @OneToMany(mappedBy = "channel",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<PlayList> playLists;

    @JsonBackReference
    @OneToOne(mappedBy = "channel")
    private User user;
}