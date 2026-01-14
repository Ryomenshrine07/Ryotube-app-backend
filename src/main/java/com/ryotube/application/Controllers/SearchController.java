package com.ryotube.application.Controllers;

import com.ryotube.application.DTOs.*;
import com.ryotube.application.Services.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(searchService.getSuggestions(q.trim()));
    }

    @GetMapping
    public ResponseEntity<SearchResultDTO> searchAll(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(new SearchResultDTO());
        }
        return ResponseEntity.ok(searchService.searchAll(q.trim()));
    }

    @GetMapping("/videos")
    public ResponseEntity<List<VideoSearchDTO>> searchVideos(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(searchService.searchVideos(q.trim()));
    }

    @GetMapping("/channels")
    public ResponseEntity<List<ChannelSearchDTO>> searchChannels(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(searchService.searchChannels(q.trim()));
    }

    @GetMapping("/shorts")
    public ResponseEntity<List<ShortSearchDTO>> searchShorts(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(searchService.searchShorts(q.trim()));
    }
}
