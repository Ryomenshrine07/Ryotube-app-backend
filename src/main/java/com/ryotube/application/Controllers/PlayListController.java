package com.ryotube.application.Controllers;

import com.ryotube.application.Entities.PlayList;
import com.ryotube.application.Services.PlayListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/playlists")
@CrossOrigin(origins = "*")
public class PlayListController {

    @Autowired
    private PlayListService playListService;

    @PostMapping("/create")
    public ResponseEntity<?> createPlaylist(@RequestBody Map<String, Object> request) {
        try {
            Long channelId = Long.valueOf(request.get("channelId").toString());
            String name = request.get("name").toString();
            String description = request.get("description") != null ? request.get("description").toString() : "";
            boolean isPublic = request.get("isPublic") != null ? Boolean.parseBoolean(request.get("isPublic").toString()) : true;

            PlayList playlist = playListService.createPlaylist(channelId, name, description, isPublic);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating playlist: " + e.getMessage());
        }
    }

    @PostMapping("/{playlistId}/videos/{videoId}")
    public ResponseEntity<?> addVideoToPlaylist(@PathVariable Long playlistId, @PathVariable Long videoId) {
        try {
            PlayList playlist = playListService.addVideoToPlaylist(playlistId, videoId);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error adding video to playlist: " + e.getMessage());
        }
    }

    @DeleteMapping("/{playlistId}/videos/{videoId}")
    public ResponseEntity<?> removeVideoFromPlaylist(@PathVariable Long playlistId, @PathVariable Long videoId) {
        try {
            PlayList playlist = playListService.removeVideoFromPlaylist(playlistId, videoId);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error removing video from playlist: " + e.getMessage());
        }
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<PlayList>> getChannelPlaylists(@PathVariable Long channelId) {
        List<PlayList> playlists = playListService.getChannelPlaylists(channelId);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/channel/{channelId}/public")
    public ResponseEntity<List<PlayList>> getPublicChannelPlaylists(@PathVariable Long channelId) {
        List<PlayList> playlists = playListService.getPublicChannelPlaylists(channelId);
        return ResponseEntity.ok(playlists);
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<?> getPlaylistById(@PathVariable Long playlistId) {
        Optional<PlayList> playlist = playListService.getPlaylistById(playlistId);
        if (playlist.isPresent()) {
            return ResponseEntity.ok(playlist.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Playlist not found");
        }
    }

    @PutMapping("/{playlistId}")
    public ResponseEntity<?> updatePlaylist(@PathVariable Long playlistId, @RequestBody Map<String, Object> request) {
        try {
            String name = request.get("name") != null ? request.get("name").toString() : null;
            String description = request.get("description") != null ? request.get("description").toString() : null;
            Boolean isPublic = request.get("isPublic") != null ? Boolean.parseBoolean(request.get("isPublic").toString()) : null;

            PlayList playlist = playListService.updatePlaylist(playlistId, name, description, isPublic);
            return ResponseEntity.ok(playlist);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating playlist: " + e.getMessage());
        }
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<?> deletePlaylist(@PathVariable Long playlistId) {
        try {
            playListService.deletePlaylist(playlistId);
            return ResponseEntity.ok("Playlist deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting playlist: " + e.getMessage());
        }
    }
}