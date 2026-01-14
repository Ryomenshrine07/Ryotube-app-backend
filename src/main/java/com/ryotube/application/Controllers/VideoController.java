package com.ryotube.application.Controllers;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Helpers.ChannelData;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.VideoRepository;
import com.ryotube.application.Services.CloudinaryService;
import com.ryotube.application.Services.FeedService;
import com.ryotube.application.Services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class VideoController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    VideoService videoService;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    FeedService feedService;

    // Frontend direct upload - accepts Cloudinary URLs
    @PostMapping("/save-video")
    public ResponseEntity<Map<String, Object>> saveVideoFromCloudinary(
            @RequestParam("videoUrl") String videoUrl,
            @RequestParam("cloudId") String cloudId,
            @RequestParam("thumbnailUrl") String thumbnailUrl,
            @RequestParam("title") String title,
            @RequestParam("duration") String duration,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestParam("channelId") Long channelId
    ) {
        try {
            Video v = new Video();
            v.setTile(title);
            v.setVideoCategory(category);
            v.setDescription(description);
            v.setVideoUrl(videoUrl);
            v.setVideoThumbnail(thumbnailUrl);
            v.setLikes(0L);
            v.setViews(0L);
            v.setCloudId(cloudId);
            v.setDislikes(0L);
            v.setDuration(duration);
            v.setFeedScore(0.0);
            
            videoService.uploadVideo(v, channelId);
            
            // Calculate initial feed score
            feedService.updateVideoFeedScore(v.getId());

            return ResponseEntity.ok(Map.of(
                    "message", "Video saved successfully!",
                    "videoId", v.getId()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error saving video: " + e.getMessage()));
        }
    }

    // NEW: Streaming upload endpoint - handles file upload through backend
    @PostMapping("/upload-video-stream")
    public ResponseEntity<Map<String, Object>> uploadVideoStream(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("tags") String tags,
            @RequestParam("channelId") Long channelId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        try {
            // Stream video to Cloudinary (memory efficient)
            Map<String, Object> videoResult = cloudinaryService.uploadVideoStream(
                    file.getInputStream(),
                    "Videos",
                    file.getOriginalFilename()
            );

            String videoUrl = (String) videoResult.get("secure_url");
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

            // Save video metadata
            String formattedDuration = videoService.formatDuration(duration);
            Video v = new Video();
            v.setTile(title);
            v.setVideoCategory(category);
            v.setDescription(description);
            v.setVideoUrl(videoUrl);
            v.setVideoThumbnail(thumbnailUrl);
            v.setLikes(0L);
            v.setViews(0L);
            v.setCloudId(cloudId);
            v.setDislikes(0L);
            v.setDuration(formattedDuration);
            videoService.uploadVideo(v, channelId);

            return ResponseEntity.ok(Map.of(
                    "message", "Video uploaded successfully!",
                    "url", videoUrl,
                    "cloudId", cloudId,
                    "duration", duration
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error uploading video: " + e.getMessage()));
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("videoId") String cloudId,
            @RequestParam("videoUrl") String videoUrl,
            @RequestParam("thumbnail") String thumbnailUrl,
            @RequestParam("title") String title,
            @RequestParam("duration") Double duration,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("tags") String tags,
            @RequestParam("channelId") String channelId
    ) {
        try {
            String formattedDuration = videoService.formatDuration(duration);
            Video v = new Video();
            v.setTile(title);
            v.setVideoCategory(category);
            v.setDescription(description);
            v.setVideoUrl(videoUrl);
            v.setVideoThumbnail(thumbnailUrl);
            v.setLikes(0L);
            v.setViews(0L);
            v.setCloudId(cloudId);
            v.setDislikes(0L);
            v.setDuration(formattedDuration);
            videoService.uploadVideo(v,Long.parseLong(channelId));

            return ResponseEntity.ok(Map.of("message", "Video uploaded successfully!", "url", videoUrl));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Error uploading file."));
        }
    }
    @PostMapping("/get-all-Channel-videos")
    public ResponseEntity<List<Video>> getAllVideosOfChannel(@RequestBody ChannelData channelData){
        try {
            System.out.println(channelData.getId());
            List<Video> videos = videoRepository.getAllChannelVideos(channelData.getId());
            return ResponseEntity.ok(videos);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/getAllVideos")
    public ResponseEntity<List<Video>> getAllVideos(){
        return ResponseEntity.ok(videoRepository.getAllVideos());
    }
    @GetMapping("/get-video-by-id/{id}")
    ResponseEntity<Video> getVideoById(@PathVariable("id") Long id){
        try {
            Video video = videoRepository.getVideoById(id);
            return ResponseEntity.ok(video);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/get-all-video-except-self/{id}")
    ResponseEntity<List<Video>> getAllVideosExceptSelf(@PathVariable("id") Long id){
        try{
            List<Video> videos = videoRepository.getAllVideosExceptSelf(id);
            return ResponseEntity.ok(videos);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/increase-view")
    void increaseVideoViews(
            @RequestParam("channelId") Long channelId,
            @RequestParam("videoId") Long videoId
    ){
        try{
            Channel c = channelRepository.getChannelById(channelId);
            Video video = videoRepository.getVideoById(videoId);
            c.getChannelStatus().setTotalViews(c.getChannelStatus().getTotalViews() + 1);
            video.setViews(video.getViews() + 1);
            videoRepository.save(video);
            channelRepository.save(c);
            // Update feed score after view increase
            feedService.updateVideoFeedScore(videoId);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    @PostMapping("/increase-like")
    void increaseVideoLike(@RequestParam("videoId") String videoId,
                           @RequestParam("channelId") String channelId){
        try{
            Long vId = Long.parseLong(videoId);
            Long cId = Long.parseLong(channelId);
            Channel c = channelRepository.getChannelById(cId);
            Video video = videoRepository.getVideoById(vId);
            if(c.getDislikedVideos().contains(vId)){
                c.getDislikedVideos().remove(vId);
                video.setDislikes(video.getDislikes() - 1);
            }
            c.getLikedVideos().add(vId);
            video.setLikes(video.getLikes() + 1);
            videoRepository.save(video);
            channelRepository.save(c);
            // Update feed score after like
            feedService.updateVideoFeedScore(vId);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @PostMapping("/decrease-like")
    void decreaseVideoLike(@RequestParam("videoId") String videoId,
                           @RequestParam("channelId") String channelId){
        try{
            Long vId = Long.parseLong(videoId);
            Long cId = Long.parseLong(channelId);
            Channel c = channelRepository.getChannelById(cId);
            Video video = videoRepository.getVideoById(vId);
            if(c.getLikedVideos().contains(vId)){
                c.getLikedVideos().remove(vId);
                video.setLikes(video.getLikes() - 1);
                videoRepository.save(video);
                channelRepository.save(c);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @PostMapping("/increase-dislike")
    void increaseVideoDislike(@RequestParam("videoId") String videoId,
                              @RequestParam("channelId") String channelId){
        try{
            Long vId = Long.parseLong(videoId);
            Long cId = Long.parseLong(channelId);
            Channel c = channelRepository.getChannelById(cId);
            Video video = videoRepository.getVideoById(vId);
            if(c.getLikedVideos().contains(vId)){
                c.getLikedVideos().remove(vId);
                video.setLikes(video.getLikes() - 1);
            }
            c.getDislikedVideos().add(vId);
            video.setDislikes(video.getDislikes() + 1);
            videoRepository.save(video);
            channelRepository.save(c);
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @PostMapping("/decrease-dislike")
    void decreaseVideoDislike(@RequestParam("videoId") String videoId,
                           @RequestParam("channelId") String channelId){
        try{
            Long vId = Long.parseLong(videoId);
            Long cId = Long.parseLong(channelId);
            Channel c = channelRepository.getChannelById(cId);
            Video video = videoRepository.getVideoById(vId);
            if(c.getDislikedVideos().contains(vId)){
                c.getDislikedVideos().remove(vId);
                video.setDislikes(video.getDislikes() - 1);
                videoRepository.save(video);
                channelRepository.save(c);
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @PostMapping("/check-liked")
    ResponseEntity<Boolean> isVideoLiked(
            @RequestParam("videoId") String videoId,
            @RequestParam("channelId") String channelId){
        try {
            Long vId = Long.parseLong(videoId);
            Long cId = Long.parseLong(channelId);
            Channel c = channelRepository.getChannelById(cId);
            return ResponseEntity.ok(c.getLikedVideos().contains(vId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/check-disliked")
    ResponseEntity<Boolean> isVideoDisliked(
            @RequestParam("videoId") String videoId,
            @RequestParam("channelId") String channelId){
        try {
            Long vId = Long.parseLong(videoId);
            Long cId = Long.parseLong(channelId);
            Channel c = channelRepository.getChannelById(cId);
            return ResponseEntity.ok(c.getDislikedVideos().contains(vId));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/get-like-count/{id}")
    ResponseEntity<Long> getLikeCount(@PathVariable("id") Long videoId){
        Video v = videoRepository.getVideoById(videoId);
        return ResponseEntity.ok(v.getLikes());
    }

    @GetMapping("/get-dislike-count/{id}")
    ResponseEntity<Long> getDislikeCount(@PathVariable("id") Long videoId){
        Video v = videoRepository.getVideoById(videoId);
        return ResponseEntity.ok(v.getDislikes());
    }
    @GetMapping("/get-video-channel-pic/{id}")
    public ResponseEntity<String> getVideoChannelPic(@PathVariable("id") Long channelId){
        try {
            Channel c = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(c.getChannelPicURL());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/get-subscription-videos/{id}")
    public ResponseEntity<List<Video>> getSubscriptionVideos(@PathVariable("id") Long channelId){
        try{
            Channel c = channelRepository.getChannelById(channelId);
            List<Video> response = new ArrayList<>();
            Set<Long> subscribedIds = c.getSubscribedChannels();
            for(Long id : subscribedIds){
                List<Video> cV = videoRepository.getAllChannelVideos(id);
                response.addAll(cV);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/download-url/{videoId}")
    public ResponseEntity<String> getDownloadUrl(@PathVariable Long videoId) {
        Video v = videoRepository.getVideoById(videoId);
        return ResponseEntity.ok(v.getVideoUrl());
    }

    @GetMapping("/get-watch-later-videos/{id}")
    public ResponseEntity<List<Video>> getWatchLaterVideos(@PathVariable("id") Long channelId){
        try{
            Channel c = channelRepository.getChannelById(channelId);
            List<Video> response = new ArrayList<>();
            for(Long id : c.getWatchLaterVideos()){
                Video v = videoRepository.getVideoById(id);
                response.add(v);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/put-video-to-watch-later")
    public void putVideoInWatchLater(
            @RequestParam("videoId") String videoId,
            @RequestParam("channelId") String channelId
    ){
        Channel c = channelRepository.getChannelById(Long.parseLong(channelId));
        c.getWatchLaterVideos().add(Long.parseLong(videoId));
        channelRepository.save(c);
    }
    @PostMapping("/remove-video-from-watch-later")
    public void removeVideoFromWatchLater(
            @RequestParam("channelId") Long channelId,
            @RequestParam("videoId") Long videoId
    ){
        Channel c = channelRepository.getChannelById(channelId);
        if(c.getWatchLaterVideos().contains(videoId)){
            c.getWatchLaterVideos().remove(videoId);
            channelRepository.save(c);
        }
    }
    @PostMapping("/check-video-watch-later")
    public ResponseEntity<Boolean> checkPresentOrNot(
            @RequestParam("videoId") String videoId,
            @RequestParam("channelId") String channelId
    ){
        try{
            Channel c = channelRepository.getChannelById(Long.parseLong(channelId));
            Set<Long> watchLater = c.getWatchLaterVideos();
            return ResponseEntity.ok(watchLater.contains(Long.parseLong(videoId)));
        } catch (Exception e) {
            e.printStackTrace();;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/update-video/{videoId}")
    public ResponseEntity<Video> updateVideo(
            @PathVariable Long videoId,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("category") String category,
            @RequestParam("channelId") Long channelId,
            @RequestParam(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        try {
            Video video = videoRepository.getVideoById(videoId);
            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Verify ownership
            if (!video.getChannelId().equals(channelId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            video.setTile(title);
            video.setDescription(description);
            video.setVideoCategory(category);
            
            // Update thumbnail if provided
            if (thumbnail != null && !thumbnail.isEmpty()) {
                Map<String, Object> thumbResult = cloudinaryService.uploadImageStream(
                        thumbnail.getInputStream(),
                        "Thumbnails"
                );
                String thumbnailUrl = (String) thumbResult.get("secure_url");
                video.setVideoThumbnail(thumbnailUrl);
            }
            
            videoRepository.save(video);
            return ResponseEntity.ok(video);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete-video/{videoId}")
    public ResponseEntity<Void> deleteVideo(
            @PathVariable Long videoId,
            @RequestParam("channelId") Long channelId
    ) {
        try {
            Video video = videoRepository.getVideoById(videoId);
            if (video == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            // Verify ownership
            if (!video.getChannelId().equals(channelId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            videoRepository.delete(video);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

