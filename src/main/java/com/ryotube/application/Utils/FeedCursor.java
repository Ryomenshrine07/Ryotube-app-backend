package com.ryotube.application.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Cursor-based pagination utility for infinite scroll feed.
 * Encodes/decodes cursor containing: feedScore|createdAt|id|type
 * This ensures stable, deterministic pagination without OFFSET.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedCursor {
    private Double feedScore;
    private LocalDateTime createdAt;
    private Long id;
    private String type; // "VIDEO", "SHORT", or "ALL"
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String DELIMITER = "|";
    
    /**
     * Encode cursor to opaque Base64 string
     */
    public String encode() {
        String raw = feedScore + DELIMITER + 
                     createdAt.format(FORMATTER) + DELIMITER + 
                     id + DELIMITER + 
                     type;
        return Base64.getUrlEncoder().encodeToString(raw.getBytes());
    }
    
    /**
     * Decode Base64 cursor string back to FeedCursor object
     */
    public static FeedCursor decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return null;
        }
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(encoded));
            String[] parts = decoded.split("\\|");
            if (parts.length != 4) {
                return null;
            }
            return new FeedCursor(
                Double.parseDouble(parts[0]),
                LocalDateTime.parse(parts[1], FORMATTER),
                Long.parseLong(parts[2]),
                parts[3]
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Create cursor from feed item data
     */
    public static FeedCursor from(Double feedScore, LocalDateTime createdAt, Long id, String type) {
        return new FeedCursor(feedScore, createdAt, id, type);
    }
}
