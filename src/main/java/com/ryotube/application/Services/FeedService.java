package com.ryotube.application.Services;

import com.ryotube.application.DTOs.FeedItemDTO;
import com.ryotube.application.DTOs.FeedResponseDTO;
import com.ryotube.application.Entities.Short;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Repositories.ShortRepository;
import com.ryotube.application.Repositories.VideoRepository;
import com.ryotube.application.Utils.FeedCursor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Feed Service implementing cursor-based infinite scroll pagination.
 * 
 * Design Choices:
 * 1. Cursor-based pagination - No OFFSET, stable scrolling, no duplicates
 * 2. Feed score calculation - Combines engagement, recency, and creator metrics
 * 3. Deterministic ordering - feedScore DESC, createdAt DESC, id DESC (tie-breaker)
 * 4. Type filtering - Support for VIDEO, SHORT, or ALL content types
 */
@Service
public class FeedService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ShortRepository shortRepository;

    // Feed score weights
    private static final double ENGAGEMENT_WEIGHT = 0.4;  // likes, views
    private static final double RECENCY_WEIGHT = 0.3;     // freshness decay
    private static final double CREATOR_WEIGHT = 0.2;     // subscriber count boost
    private static final double COMMENT_WEIGHT = 0.1;     // comment engagement

    /**
     * Get paginated feed with cursor-based pagination
     * 
     * @param cursor - Opaque cursor string (null for first page)
     * @param limit - Number of items per page
     * @param type - "VIDEO", "SHORT", or "ALL"
     * @return FeedResponseDTO with items, nextCursor, and hasMore flag
     */
    public FeedResponseDTO getFeed(String cursor, int limit, String type) {
        FeedCursor decodedCursor = FeedCursor.decode(cursor);
        List<FeedItemDTO> items = new ArrayList<>();

        if ("VIDEO".equalsIgnoreCase(type)) {
            items = getVideoFeed(decodedCursor, limit + 1);
        } else if ("SHORT".equalsIgnoreCase(type)) {
            items = getShortFeed(decodedCursor, limit + 1);
        } else {
            // ALL - merge both feeds
            items = getMixedFeed(decodedCursor, limit + 1);
        }

        boolean hasMore = items.size() > limit;
        if (hasMore) {
            items = items.subList(0, limit);
        }

        String nextCursor = null;
        if (!items.isEmpty() && hasMore) {
            FeedItemDTO lastItem = items.get(items.size() - 1);
            nextCursor = FeedCursor.from(
                lastItem.getFeedScore(),
                lastItem.getCreatedAt(),
                lastItem.getId(),
                type
            ).encode();
        }

        return FeedResponseDTO.builder()
                .items(items)
                .nextCursor(nextCursor)
                .hasMore(hasMore)
                .build();
    }

    /**
     * Get video-only feed with cursor pagination
     */
    private List<FeedItemDTO> getVideoFeed(FeedCursor cursor, int limit) {
        String jpql;
        TypedQuery<Video> query;

        if (cursor == null) {
            jpql = "SELECT v FROM Video v ORDER BY v.feedScore DESC, v.uploadDateTime DESC, v.id DESC";
            query = entityManager.createQuery(jpql, Video.class);
        } else {
            jpql = """
                SELECT v FROM Video v 
                WHERE v.feedScore < :lastScore 
                   OR (v.feedScore = :lastScore AND v.uploadDateTime < :lastCreatedAt)
                   OR (v.feedScore = :lastScore AND v.uploadDateTime = :lastCreatedAt AND v.id < :lastId)
                ORDER BY v.feedScore DESC, v.uploadDateTime DESC, v.id DESC
                """;
            query = entityManager.createQuery(jpql, Video.class)
                    .setParameter("lastScore", cursor.getFeedScore())
                    .setParameter("lastCreatedAt", cursor.getCreatedAt())
                    .setParameter("lastId", cursor.getId());
        }

        return query.setMaxResults(limit)
                .getResultList()
                .stream()
                .map(this::mapVideoToFeedItem)
                .collect(Collectors.toList());
    }

    /**
     * Get short-only feed with cursor pagination
     */
    private List<FeedItemDTO> getShortFeed(FeedCursor cursor, int limit) {
        String jpql;
        TypedQuery<Short> query;

        if (cursor == null) {
            jpql = "SELECT s FROM Short s ORDER BY s.feedScore DESC, s.uploadDateTime DESC, s.id DESC";
            query = entityManager.createQuery(jpql, Short.class);
        } else {
            jpql = """
                SELECT s FROM Short s 
                WHERE s.feedScore < :lastScore 
                   OR (s.feedScore = :lastScore AND s.uploadDateTime < :lastCreatedAt)
                   OR (s.feedScore = :lastScore AND s.uploadDateTime = :lastCreatedAt AND s.id < :lastId)
                ORDER BY s.feedScore DESC, s.uploadDateTime DESC, s.id DESC
                """;
            query = entityManager.createQuery(jpql, Short.class)
                    .setParameter("lastScore", cursor.getFeedScore())
                    .setParameter("lastCreatedAt", cursor.getCreatedAt())
                    .setParameter("lastId", cursor.getId());
        }

        return query.setMaxResults(limit)
                .getResultList()
                .stream()
                .map(this::mapShortToFeedItem)
                .collect(Collectors.toList());
    }

    /**
     * Get mixed feed (videos + shorts) with cursor pagination
     * Fetches from both sources and merges by feedScore
     */
    private List<FeedItemDTO> getMixedFeed(FeedCursor cursor, int limit) {
        // Fetch more than needed from each source to ensure proper merging
        List<FeedItemDTO> videos = getVideoFeed(cursor, limit);
        List<FeedItemDTO> shorts = getShortFeed(cursor, limit);

        List<FeedItemDTO> merged = new ArrayList<>();
        merged.addAll(videos);
        merged.addAll(shorts);

        // Sort by feedScore DESC, createdAt DESC, id DESC
        merged.sort((a, b) -> {
            int scoreCompare = Double.compare(b.getFeedScore(), a.getFeedScore());
            if (scoreCompare != 0) return scoreCompare;
            
            int dateCompare = b.getCreatedAt().compareTo(a.getCreatedAt());
            if (dateCompare != 0) return dateCompare;
            
            return Long.compare(b.getId(), a.getId());
        });

        // Apply cursor filter for mixed feed
        if (cursor != null) {
            merged = merged.stream()
                .filter(item -> {
                    int scoreCompare = Double.compare(item.getFeedScore(), cursor.getFeedScore());
                    if (scoreCompare < 0) return true;
                    if (scoreCompare > 0) return false;
                    
                    int dateCompare = item.getCreatedAt().compareTo(cursor.getCreatedAt());
                    if (dateCompare < 0) return true;
                    if (dateCompare > 0) return false;
                    
                    return item.getId() < cursor.getId();
                })
                .collect(Collectors.toList());
        }

        return merged.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * Calculate feed score for a video
     * Formula: engagement(0.4) + recency(0.3) + creator(0.2) + comments(0.1)
     */
    public double calculateVideoFeedScore(Video video) {
        // Engagement score (normalized)
        double engagementScore = normalizeEngagement(video.getViews(), video.getLikes());
        
        // Recency score (decay over time)
        double recencyScore = calculateRecencyScore(video.getUploadDateTime());
        
        // Creator boost (based on subscriber count)
        double creatorScore = normalizeCreatorScore(
            video.getChannel() != null ? video.getChannel().getSubscribersCount() : 0L
        );
        
        // Comment engagement
        double commentScore = normalizeCommentScore(
            video.getComments() != null ? video.getComments().size() : 0
        );

        return (engagementScore * ENGAGEMENT_WEIGHT) +
               (recencyScore * RECENCY_WEIGHT) +
               (creatorScore * CREATOR_WEIGHT) +
               (commentScore * COMMENT_WEIGHT);
    }

    /**
     * Calculate feed score for a short
     */
    public double calculateShortFeedScore(Short shortVideo) {
        double engagementScore = normalizeEngagement(shortVideo.getViews(), shortVideo.getLikes());
        double recencyScore = calculateRecencyScore(shortVideo.getUploadDateTime());
        double creatorScore = normalizeCreatorScore(
            shortVideo.getChannel() != null ? shortVideo.getChannel().getSubscribersCount() : 0L
        );
        double commentScore = normalizeCommentScore(
            shortVideo.getComments() != null ? shortVideo.getComments().size() : 0
        );

        return (engagementScore * ENGAGEMENT_WEIGHT) +
               (recencyScore * RECENCY_WEIGHT) +
               (creatorScore * CREATOR_WEIGHT) +
               (commentScore * COMMENT_WEIGHT);
    }

    /**
     * Normalize engagement metrics to 0-1 scale
     */
    private double normalizeEngagement(long views, long likes) {
        // Log scale to handle viral content
        double viewScore = views > 0 ? Math.log10(views + 1) / 10.0 : 0;
        double likeScore = likes > 0 ? Math.log10(likes + 1) / 8.0 : 0;
        return Math.min(1.0, (viewScore * 0.6) + (likeScore * 0.4));
    }

    /**
     * Calculate recency score with exponential decay
     * Newer content scores higher
     */
    private double calculateRecencyScore(LocalDateTime uploadTime) {
        if (uploadTime == null) return 0.0;
        long hoursAgo = ChronoUnit.HOURS.between(uploadTime, LocalDateTime.now());
        // Exponential decay: score halves every 48 hours
        return Math.exp(-hoursAgo / 48.0);
    }

    /**
     * Normalize creator score based on subscriber count
     */
    private double normalizeCreatorScore(Long subscriberCount) {
        if (subscriberCount == null || subscriberCount <= 0) return 0.1;
        // Log scale for subscriber count
        return Math.min(1.0, Math.log10(subscriberCount + 1) / 6.0);
    }

    /**
     * Normalize comment count
     */
    private double normalizeCommentScore(int commentCount) {
        if (commentCount <= 0) return 0.0;
        return Math.min(1.0, Math.log10(commentCount + 1) / 4.0);
    }

    /**
     * Map Video entity to FeedItemDTO
     */
    private FeedItemDTO mapVideoToFeedItem(Video video) {
        return FeedItemDTO.builder()
                .id(video.getId())
                .type("VIDEO")
                .title(video.getTile())
                .description(video.getDescription())
                .thumbnail(video.getVideoThumbnail())
                .url(video.getVideoUrl())
                .duration(video.getDuration())
                .views(video.getViews())
                .likes(video.getLikes())
                .dislikes(video.getDislikes())
                .feedScore(video.getFeedScore() != null ? video.getFeedScore() : 0.0)
                .createdAt(video.getUploadDateTime())
                .channelId(video.getChannelId())
                .channelName(video.getChannelName())
                .channelPicUrl(video.getChannel() != null ? video.getChannel().getChannelPicURL() : null)
                .subscriberCount(video.getChannelSubscriberCount())
                .commentCount(video.getComments() != null ? video.getComments().size() : 0)
                .build();
    }

    /**
     * Map Short entity to FeedItemDTO
     */
    private FeedItemDTO mapShortToFeedItem(Short shortVideo) {
        return FeedItemDTO.builder()
                .id(shortVideo.getId())
                .type("SHORT")
                .title(shortVideo.getTitle())
                .description(shortVideo.getDescription())
                .thumbnail(shortVideo.getShortThumbnail())
                .url(shortVideo.getShortUrl())
                .duration(shortVideo.getDuration())
                .views(shortVideo.getViews())
                .likes(shortVideo.getLikes())
                .dislikes(shortVideo.getDislikes())
                .feedScore(shortVideo.getFeedScore() != null ? shortVideo.getFeedScore() : 0.0)
                .createdAt(shortVideo.getUploadDateTime())
                .channelId(shortVideo.getChannelId())
                .channelName(shortVideo.getChannelName())
                .channelPicUrl(shortVideo.getChannelPicUrl())
                .subscriberCount(shortVideo.getChannel() != null ? shortVideo.getChannel().getSubscribersCount() : 0L)
                .commentCount(shortVideo.getComments() != null ? shortVideo.getComments().size() : 0)
                .build();
    }

    /**
     * Batch update feed scores for all videos
     * Should be called periodically (e.g., every hour via scheduled job)
     */
    @Transactional
    public void updateAllVideoFeedScores() {
        List<Video> videos = videoRepository.findAll();
        for (Video video : videos) {
            video.setFeedScore(calculateVideoFeedScore(video));
            videoRepository.save(video);
        }
    }

    /**
     * Batch update feed scores for all shorts
     */
    @Transactional
    public void updateAllShortFeedScores() {
        List<Short> shorts = shortRepository.findAll();
        for (Short shortVideo : shorts) {
            shortVideo.setFeedScore(calculateShortFeedScore(shortVideo));
            shortRepository.save(shortVideo);
        }
    }

    /**
     * Update feed score for a single video (call after engagement changes)
     */
    @Transactional
    public void updateVideoFeedScore(Long videoId) {
        Video video = videoRepository.getVideoById(videoId);
        if (video != null) {
            video.setFeedScore(calculateVideoFeedScore(video));
            videoRepository.save(video);
        }
    }

    /**
     * Update feed score for a single short
     */
    @Transactional
    public void updateShortFeedScore(Long shortId) {
        Short shortVideo = shortRepository.getShortById(shortId);
        if (shortVideo != null) {
            shortVideo.setFeedScore(calculateShortFeedScore(shortVideo));
            shortRepository.save(shortVideo);
        }
    }
}
