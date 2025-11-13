package com.ryotube.application.Controllers;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Helpers.ChannelData;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.VideoRepository;
import com.ryotube.application.Services.CloudinaryService;
import com.ryotube.application.Services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
}

