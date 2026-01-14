package com.ryotube.application.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedResponseDTO {
    private List<FeedItemDTO> items;
    private String nextCursor;
    private boolean hasMore;
}
