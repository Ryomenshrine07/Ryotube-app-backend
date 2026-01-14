package com.ryotube.application.Controllers;

import com.ryotube.application.DTOs.FeedResponseDTO;
import com.ryotube.application.Services.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feed Controller for infinite scroll pagination.
 * 
 * Endpoints:
 * - GET /api/feed - Get paginated feed with cursor-based pagination
 * - POST /api/feed/refresh-scores - Manually trigger feed score recalculation
 * 
 * Query Parameters:
 * - cursor: Opaque Base64 encoded cursor (null for first page)
 * - limit: Number of items per page (default 10, max 50)
 * - type: Content type filter - "VIDEO", "SHORT", or "ALL" (default)
 */
@RestController
@RequestMapping("/api/feed")
@CrossOrigin(origins = "*")
public class FeedController {

    @Autowired
    private FeedService feedService;

    /**
     * Get paginated feed
     * 
     * @param cursor - Opaque cursor from previous response (null for first page)
     * @param limit - Items per page (default 10)
     * @param type - "VIDEO", "SHORT", or "ALL"
     * @return FeedResponseDTO with items, nextCursor, and hasMore flag
     */
    @GetMapping
    public ResponseEntity<FeedResponseDTO> getFeed(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "ALL") String type
    ) {
        // Clamp limit to reasonable bounds
        limit = Math.max(1, Math.min(limit, 50));
        
        FeedResponseDTO response = feedService.getFeed(cursor, limit, type);
        return ResponseEntity.ok(response);
    }

    /**
     * Get video-only feed (convenience endpoint)
     */
    @GetMapping("/videos")
    public ResponseEntity<FeedResponseDTO> getVideoFeed(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        limit = Math.max(1, Math.min(limit, 50));
        return ResponseEntity.ok(feedService.getFeed(cursor, limit, "VIDEO"));
    }

    /**
     * Get shorts-only feed (convenience endpoint)
     */
    @GetMapping("/shorts")
    public ResponseEntity<FeedResponseDTO> getShortsFeed(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int limit
    ) {
        limit = Math.max(1, Math.min(limit, 50));
        return ResponseEntity.ok(feedService.getFeed(cursor, limit, "SHORT"));
    }

    /**
     * Manually refresh all feed scores
     * In production, this would be a scheduled job or admin-only endpoint
     */
    @PostMapping("/refresh-scores")
    public ResponseEntity<String> refreshFeedScores() {
        feedService.updateAllVideoFeedScores();
        feedService.updateAllShortFeedScores();
        return ResponseEntity.ok("Feed scores updated successfully");
    }

    /**
     * Refresh feed score for a specific video
     */
    @PostMapping("/refresh-video/{videoId}")
    public ResponseEntity<String> refreshVideoScore(@PathVariable Long videoId) {
        feedService.updateVideoFeedScore(videoId);
        return ResponseEntity.ok("Video feed score updated");
    }

    /**
     * Refresh feed score for a specific short
     */
    @PostMapping("/refresh-short/{shortId}")
    public ResponseEntity<String> refreshShortScore(@PathVariable Long shortId) {
        feedService.updateShortFeedScore(shortId);
        return ResponseEntity.ok("Short feed score updated");
    }
}
