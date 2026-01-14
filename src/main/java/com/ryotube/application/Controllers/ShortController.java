package com.ryotube.application.Controllers;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Short;
import com.ryotube.application.Entities.ShortComment;
import com.ryotube.application.Entities.ShortCommentReply;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.ShortRepository;
import com.ryotube.application.Repositories.ShortCommentRepository;
import com.ryotube.application.Repositories.ShortCommentReplyRepository;
import com.ryotube.application.Services.CloudinaryService;
import com.ryotube.application.Services.FeedService;
import com.ryotube.application.Services.ShortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shorts")
public class ShortController {

    @Autowired
    private ShortService shortService;

    @Autowired
    private ShortRepository shortRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ShortCommentRepository shortCommentRepository;

    @Autowired
    private ShortCommentReplyRepository shortCommentReplyRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private FeedService feedService;

    // Frontend direct upload - accepts Cloudinary URLs
    @PostMapping("/save")
    public ResponseEntity<Map<String, Object>> saveShortFromCloudinary(
            @RequestParam("shortUrl") String shortUrl,
            @RequestParam("cloudId") String cloudId,
            @RequestParam("thumbnailUrl") String thumbnailUrl,
            @RequestParam("title") String title,
            @RequestParam("duration") String duration,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("channelId") Long channelId
    ) {
        try {
            Short s = new Short();
            s.setTitle(title);
            s.setShortCategory(category);
            s.setDescription(description);
            s.setShortUrl(shortUrl);
            s.setShortThumbnail(thumbnailUrl);
            s.setLikes(0L);
            s.setViews(0L);
            s.setCloudId(cloudId);
            s.setDislikes(0L);
            s.setDuration(duration);
            s.setFeedScore(0.0);
            
            shortService.uploadShort(s, channelId);
            
            // Calculate initial feed score
            feedService.updateShortFeedScore(s.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "Short saved successfully!",
                    "shortId", s.getId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error saving short: " + e.getMessage()));
        }
    }

    // NEW: Streaming upload endpoint - handles file upload through backend
    @PostMapping("/upload-stream")
    public ResponseEntity<Map<String, Object>> uploadShortStream(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("channelId") Long channelId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        try {
            // Stream video to Cloudinary (memory efficient)
            Map<String, Object> videoResult = cloudinaryService.uploadVideoStream(
                    file.getInputStream(),
                    "Shorts",
                    file.getOriginalFilename()
            );

            String shortUrl = (String) videoResult.get("secure_url");
            String cloudId = (String) videoResult.get("public_id");
            Number durationNum = (Number) videoResult.get("duration");
            double duration = durationNum != null ? durationNum.doubleValue() : 0;

            // Upload thumbnail if provided
            String thumbnailUrl = "";
            if (thumbnail != null && !thumbnail.isEmpty()) {
                Map<String, Object> thumbResult = cloudinaryService.uploadImageStream(
                        thumbnail.getInputStream(),
                        "Thumbnails"
                );
                thumbnailUrl = (String) thumbResult.get("secure_url");
            }

            // Save short metadata
            String formattedDuration = shortService.formatDuration(duration);
            Short s = new Short();
            s.setTitle(title);
            s.setShortCategory(category);
            s.setDescription(description);
            s.setShortUrl(shortUrl);
            s.setShortThumbnail(thumbnailUrl);
            s.setLikes(0L);
            s.setViews(0L);
            s.setCloudId(cloudId);
            s.setDislikes(0L);
            s.setDuration(formattedDuration);
            
            shortService.uploadShort(s, channelId);

            return ResponseEntity.ok(Map.of(
                    "message", "Short uploaded successfully!",
                    "url", shortUrl,
                    "cloudId", cloudId,
                    "duration", duration
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error uploading short: " + e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadShort(
            @RequestParam("cloudId") String cloudId,
            @RequestParam("shortUrl") String shortUrl,
            @RequestParam("thumbnail") String thumbnailUrl,
            @RequestParam("title") String title,
            @RequestParam("duration") Double duration,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("channelId") String channelId
    ) {
        try {
            String formattedDuration = shortService.formatDuration(duration);
            Short s = new Short();
            s.setTitle(title);
            s.setShortCategory(category);
            s.setDescription(description);
            s.setShortUrl(shortUrl);
            s.setShortThumbnail(thumbnailUrl);
            s.setLikes(0L);
            s.setViews(0L);
            s.setCloudId(cloudId);
            s.setDislikes(0L);
            s.setDuration(formattedDuration);
            
            shortService.uploadShort(s, Long.parseLong(channelId));

            return ResponseEntity.ok(Map.of("message", "Short uploaded successfully!", "url", shortUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error uploading short."));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Short>> getAllShorts() {
        return ResponseEntity.ok(shortRepository.getAllShorts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Short> getShortById(@PathVariable Long id) {
        try {
            Short shortVideo = shortRepository.getShortById(id);
            return ResponseEntity.ok(shortVideo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<Short>> getChannelShorts(@PathVariable Long channelId) {
        try {
            List<Short> shorts = shortRepository.getAllChannelShorts(channelId);
            return ResponseEntity.ok(shorts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/feed/{id}")
    public ResponseEntity<List<Short>> getShortsFeed(@PathVariable Long id) {
        try {
            List<Short> shorts = shortRepository.getAllShortsExceptSelf(id);
            return ResponseEntity.ok(shorts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/view")
    public void increaseShortViews(@PathVariable Long id) {
        try {
            Short shortVideo = shortRepository.getShortById(id);
            shortVideo.setViews(shortVideo.getViews() + 1);
            shortRepository.save(shortVideo);
            // Update feed score after view increase
            feedService.updateShortFeedScore(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/{id}/like")
    public void likeShort(@PathVariable Long id, @RequestParam Long channelId) {
        try {
            Short shortVideo = shortRepository.getShortById(id);
            Channel channel = channelRepository.getChannelById(channelId);
            
            if (channel.getLikedShorts().contains(id)) {
                return;
            }
            
            if (channel.getDislikedShorts().contains(id)) {
                channel.getDislikedShorts().remove(id);
                shortVideo.setDislikes(shortVideo.getDislikes() - 1);
            }
            
            channel.getLikedShorts().add(id);
            shortVideo.setLikes(shortVideo.getLikes() + 1);
            
            shortRepository.save(shortVideo);
            channelRepository.save(channel);
            // Update feed score after like
            feedService.updateShortFeedScore(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/{id}/unlike")
    public void unlikeShort(@PathVariable Long id, @RequestParam Long channelId) {
        try {
            Short shortVideo = shortRepository.getShortById(id);
            Channel channel = channelRepository.getChannelById(channelId);
            
            if (channel.getLikedShorts().contains(id)) {
                channel.getLikedShorts().remove(id);
                shortVideo.setLikes(shortVideo.getLikes() - 1);
                shortRepository.save(shortVideo);
                channelRepository.save(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/{id}/dislike")
    public void dislikeShort(@PathVariable Long id, @RequestParam Long channelId) {
        try {
            Short shortVideo = shortRepository.getShortById(id);
            Channel channel = channelRepository.getChannelById(channelId);
            
            if (channel.getDislikedShorts().contains(id)) {
                return;
            }
            
            if (channel.getLikedShorts().contains(id)) {
                channel.getLikedShorts().remove(id);
                shortVideo.setLikes(shortVideo.getLikes() - 1);
            }
            
            channel.getDislikedShorts().add(id);
            shortVideo.setDislikes(shortVideo.getDislikes() + 1);
            
            shortRepository.save(shortVideo);
            channelRepository.save(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/{id}/isLiked")
    public ResponseEntity<Boolean> isShortLiked(@PathVariable Long id, @RequestParam Long channelId) {
        try {
            Channel channel = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(channel.getLikedShorts().contains(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/{id}/isDisliked")
    public ResponseEntity<Boolean> isShortDisliked(@PathVariable Long id, @RequestParam Long channelId) {
        try {
            Channel channel = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(channel.getDislikedShorts().contains(id));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(false);
        }
    }

    // ============ COMMENT ENDPOINTS ============

    @GetMapping("/{shortId}/comments")
    public ResponseEntity<List<ShortComment>> getShortComments(@PathVariable Long shortId) {
        try {
            List<ShortComment> comments = shortCommentRepository.getCommentsByShortId(shortId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{shortId}/comments")
    public ResponseEntity<ShortComment> postComment(
            @PathVariable Long shortId,
            @RequestParam String commentData,
            @RequestParam Long channelId
    ) {
        try {
            Short shortVideo = shortRepository.getShortById(shortId);
            Channel channel = channelRepository.getChannelById(channelId);

            ShortComment comment = new ShortComment();
            comment.setCommentData(commentData);
            comment.setUsername(channel.getChannelName());
            comment.setUserPicURL(channel.getChannelPicURL());
            comment.setUserChannelId(channelId);
            comment.setLikes(0L);
            comment.setDisLikes(0L);
            comment.setShortVideo(shortVideo);

            shortCommentRepository.save(comment);
            return ResponseEntity.ok(comment);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        try {
            shortCommentRepository.deleteById(commentId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/comments/{commentId}/like")
    public void likeComment(@PathVariable Long commentId, @RequestParam Long channelId) {
        try {
            ShortComment comment = shortCommentRepository.getCommentById(commentId);
            Channel channel = channelRepository.getChannelById(channelId);

            if (channel.getLikedShortComments().contains(commentId)) return;

            if (channel.getDislikedShortComments().contains(commentId)) {
                channel.getDislikedShortComments().remove(commentId);
                comment.setDisLikes(comment.getDisLikes() - 1);
            }

            channel.getLikedShortComments().add(commentId);
            comment.setLikes(comment.getLikes() + 1);

            shortCommentRepository.save(comment);
            channelRepository.save(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/comments/{commentId}/unlike")
    public void unlikeComment(@PathVariable Long commentId, @RequestParam Long channelId) {
        try {
            ShortComment comment = shortCommentRepository.getCommentById(commentId);
            Channel channel = channelRepository.getChannelById(channelId);

            if (channel.getLikedShortComments().contains(commentId)) {
                channel.getLikedShortComments().remove(commentId);
                comment.setLikes(comment.getLikes() - 1);
                shortCommentRepository.save(comment);
                channelRepository.save(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/comments/{commentId}/dislike")
    public void dislikeComment(@PathVariable Long commentId, @RequestParam Long channelId) {
        try {
            ShortComment comment = shortCommentRepository.getCommentById(commentId);
            Channel channel = channelRepository.getChannelById(channelId);

            if (channel.getDislikedShortComments().contains(commentId)) return;

            if (channel.getLikedShortComments().contains(commentId)) {
                channel.getLikedShortComments().remove(commentId);
                comment.setLikes(comment.getLikes() - 1);
            }

            channel.getDislikedShortComments().add(commentId);
            comment.setDisLikes(comment.getDisLikes() + 1);

            shortCommentRepository.save(comment);
            channelRepository.save(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/comments/{commentId}/isLiked")
    public ResponseEntity<Boolean> isCommentLiked(@PathVariable Long commentId, @RequestParam Long channelId) {
        try {
            Channel channel = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(channel.getLikedShortComments().contains(commentId));
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/comments/{commentId}/isDisliked")
    public ResponseEntity<Boolean> isCommentDisliked(@PathVariable Long commentId, @RequestParam Long channelId) {
        try {
            Channel channel = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(channel.getDislikedShortComments().contains(commentId));
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }

    // ============ REPLY ENDPOINTS ============

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<ShortCommentReply> postReply(
            @PathVariable Long commentId,
            @RequestParam String commentData,
            @RequestParam Long channelId
    ) {
        try {
            ShortComment comment = shortCommentRepository.getCommentById(commentId);
            Channel channel = channelRepository.getChannelById(channelId);

            ShortCommentReply reply = new ShortCommentReply();
            reply.setCommentData(commentData);
            reply.setUsername(channel.getChannelName());
            reply.setUserPicURL(channel.getChannelPicURL());
            reply.setUserChannelId(channelId);
            reply.setLikes(0L);
            reply.setDisLikes(0L);
            reply.setComment(comment);

            shortCommentReplyRepository.save(reply);
            return ResponseEntity.ok(reply);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<Void> deleteReply(@PathVariable Long replyId) {
        try {
            shortCommentReplyRepository.deleteById(replyId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/replies/{replyId}/like")
    public void likeReply(@PathVariable Long replyId, @RequestParam Long channelId) {
        try {
            ShortCommentReply reply = shortCommentReplyRepository.getReplyById(replyId);
            Channel channel = channelRepository.getChannelById(channelId);

            if (channel.getLikedShortCommentReplies().contains(replyId)) return;

            if (channel.getDislikedShortCommentReplies().contains(replyId)) {
                channel.getDislikedShortCommentReplies().remove(replyId);
                reply.setDisLikes(reply.getDisLikes() - 1);
            }

            channel.getLikedShortCommentReplies().add(replyId);
            reply.setLikes(reply.getLikes() + 1);

            shortCommentReplyRepository.save(reply);
            channelRepository.save(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/replies/{replyId}/dislike")
    public void dislikeReply(@PathVariable Long replyId, @RequestParam Long channelId) {
        try {
            ShortCommentReply reply = shortCommentReplyRepository.getReplyById(replyId);
            Channel channel = channelRepository.getChannelById(channelId);

            if (channel.getDislikedShortCommentReplies().contains(replyId)) return;

            if (channel.getLikedShortCommentReplies().contains(replyId)) {
                channel.getLikedShortCommentReplies().remove(replyId);
                reply.setLikes(reply.getLikes() - 1);
            }

            channel.getDislikedShortCommentReplies().add(replyId);
            reply.setDisLikes(reply.getDisLikes() + 1);

            shortCommentReplyRepository.save(reply);
            channelRepository.save(channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PutMapping("/{shortId}/update")
    public ResponseEntity<Short> updateShort(
            @PathVariable Long shortId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("channelId") Long channelId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        try {
            Short shortVideo = shortRepository.getShortById(shortId);
            if (shortVideo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Verify ownership
            if (!shortVideo.getChannelId().equals(channelId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            shortVideo.setTitle(title);
            shortVideo.setDescription(description);
            shortVideo.setShortCategory(category);
            
            // Update thumbnail if provided
            if (thumbnail != null && !thumbnail.isEmpty()) {
                Map<String, Object> thumbResult = cloudinaryService.uploadImageStream(
                        thumbnail.getInputStream(),
                        "Thumbnails"
                );
                String thumbnailUrl = (String) thumbResult.get("secure_url");
                shortVideo.setShortThumbnail(thumbnailUrl);
            }
            
            shortRepository.save(shortVideo);
            return ResponseEntity.ok(shortVideo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{shortId}/delete")
    public ResponseEntity<Void> deleteShort(
            @PathVariable Long shortId,
            @RequestParam("channelId") Long channelId
    ) {
        try {
            Short shortVideo = shortRepository.getShortById(shortId);
            if (shortVideo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Verify ownership
            if (!shortVideo.getChannelId().equals(channelId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            shortRepository.delete(shortVideo);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
