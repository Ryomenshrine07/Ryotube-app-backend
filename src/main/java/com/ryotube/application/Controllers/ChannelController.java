package com.ryotube.application.Controllers;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Helpers.SubscriptionElement;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.VideoRepository;
import com.ryotube.application.Services.ChannelService;
import com.ryotube.application.Services.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class ChannelController {

    @Autowired
    VideoRepository videoRepository;

    @Value("${aws.s3.bucket.thumbnails}")
    private String thumbnailBucketName;

    @Autowired
    private S3Service s3Service;
    @Autowired
    private ChannelRepository channelRepository;

    @PostMapping("/update-profile-pic")
    public ResponseEntity<?> updateChannelAvatar(
            @RequestParam("channelId") Long channelId,
            @RequestParam("profile-pic")MultipartFile file
    ){
        try{
            String profileURL = s3Service.uploadFile(file,thumbnailBucketName);
            Channel c = channelRepository.getChannelById(channelId);
            c.setChannelPicURL(profileURL);
            channelRepository.save(c);
            return ResponseEntity.ok("Updated Successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/update-banner-pic")
    public ResponseEntity<?> updateChannelBannerPic(
            @RequestParam("channelId") Long channelId,
            @RequestParam("banner-pic")MultipartFile file,
            @RequestParam("bannerHead") String bannerHead,
            @RequestParam("bannerDesc") String bannerDesc
    ){
        try{
            String bannerPicURL = s3Service.uploadFile(file,thumbnailBucketName);
            Channel c = channelRepository.getChannelById(channelId);
            if(!bannerPicURL.isEmpty()){
                c.getBanner().setBannerPicUrl(bannerPicURL);
            }
            c.getBanner().setBannerHead(bannerHead);
            c.getBanner().setBannerDescription(bannerDesc);
            channelRepository.save(c);
            return ResponseEntity.ok("Updated Successfully");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/update-about")
    public void updateChannelAbout(
            @RequestParam("channelId") Long channelId,
            @RequestParam("channelDesc") String channelDesc,
            @RequestParam("website-link") String websiteLink,
            @RequestParam("twitter-link") String twitterLink,
            @RequestParam("github-link") String gitHubLink
    ){
        Channel c = channelRepository.getChannelById(channelId);
        if(!channelDesc.isEmpty()){
            c.setChannelDescription(channelDesc);
        }
        if(!websiteLink.isEmpty()){
            c.setWebsiteLink(websiteLink);
        }
        if(!twitterLink.isEmpty()){
            c.setTwitterLink(twitterLink);
        }
        if(!gitHubLink.isEmpty()){
            c.setGitHubLink(gitHubLink);
        }
        channelRepository.save(c);
    }
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeToChannel(
            @RequestParam("channelId") Long channelId,
            @RequestParam("videoId") Long videoId
    ){
        try{
            Channel c = channelRepository.getChannelById(channelId);
            Video v = videoRepository.getVideoById(videoId);
            Long videoChannelId = v.getChannelId();
            c.getSubscribedChannels().add(videoChannelId);
            Channel vc = channelRepository.getChannelById(videoChannelId);
            vc.setSubscribersCount(vc.getSubscribersCount() + 1);
            channelRepository.save(c);
            channelRepository.save(vc);
            return ResponseEntity.ok("Subscribed to the Channel");
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/subscribe-channel")
    public ResponseEntity<?> subscribeToChannelPage(
            @RequestParam("userChannelId") Long userChannelId,
            @RequestParam("subChannelId") Long subChannelId
    ){
        try{
            Channel c = channelRepository.getChannelById(userChannelId);
            c.getSubscribedChannels().add(subChannelId);
            Channel vc = channelRepository.getChannelById(subChannelId);
            vc.setSubscribersCount(vc.getSubscribersCount() + 1);
            channelRepository.save(c);
            channelRepository.save(vc);
            return ResponseEntity.ok("Subscribed to the Channel");
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/unsubscribe-channel")
    public ResponseEntity<?> unSubscribeToChannelPage(
            @RequestParam("userChannelId") Long userChannelId,
            @RequestParam("subChannelId") Long subChannelId
    ){
        try{
            Channel c = channelRepository.getChannelById(userChannelId);
            c.getSubscribedChannels().remove(subChannelId);
            Channel vc = channelRepository.getChannelById(subChannelId);
            vc.setSubscribersCount(vc.getSubscribersCount() - 1);
            channelRepository.save(c);
            channelRepository.save(vc);
            return ResponseEntity.ok("Subscribed to the Channel");
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/unsubscribe")
    public void unsubscribeChannel(
            @RequestParam("channelId") Long channelId,
            @RequestParam("videoId") Long videoId
            ){
        Channel c = channelRepository.getChannelById(channelId);
        Video v = videoRepository.getVideoById(videoId);
        Channel vc = channelRepository.getChannelById(v.getChannelId());
        vc.setSubscribersCount(vc.getSubscribersCount() - 1);
        c.getSubscribedChannels().remove(v.getChannelId());
        channelRepository.save(c);
        channelRepository.save(vc);
    }
    @PostMapping("/check-subscribe")
    public ResponseEntity<Boolean> checkChannelSubscribed(
            @RequestParam("channelId") Long channelId,
            @RequestParam("subscribed-channelId") Long subChannelId
    ){
        try{
            Channel c = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(c.getSubscribedChannels().contains(subChannelId));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @GetMapping("/get-sub-count/{id}")
    public ResponseEntity<Long> getSubCount(@PathVariable("id") Long channelId){
        Channel c = channelRepository.getChannelById(channelId);
        return ResponseEntity.ok(c.getSubscribersCount());
    }
    @GetMapping("/get-subscriptions/{id}")
    public ResponseEntity<List<SubscriptionElement>> getSubscriptions(@PathVariable("id") Long channelId){
        try {
            Channel c = channelRepository.getChannelById(channelId);
            Set<Long> subscriptionIds = c.getSubscribedChannels();
            List<SubscriptionElement> subscribed = new ArrayList<>();
            for(Long id : subscriptionIds){
                Channel channel = channelRepository.getChannelById(id);
                List<Video> videos = videoRepository.getAllChannelVideos(channel.getId());
                SubscriptionElement se = new SubscriptionElement();
                se.setChannelId(channel.getId());
                se.setChannelName(channel.getChannelName());
                se.setChannelPicURL(channel.getChannelPicURL());
                se.setSubscriberCount(channel.getSubscribersCount());
                se.setVideosCount((long) videos.size());
                subscribed.add(se);
            }
            return ResponseEntity.ok(subscribed);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/get-liked-videos/{id}")
    public ResponseEntity<List<Video>> getLikedVideos(@PathVariable("id") Long channelId){
        try {
            Channel c = channelRepository.getChannelById(channelId);
            Set<Long> likedVideos = c.getLikedVideos();
            List<Video> response = new ArrayList<>();
            for(Long id : likedVideos){
                Video v = videoRepository.getVideoById(id);
                response.add(v);
            }
            return ResponseEntity.ok(response);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/set-video-to-history")
    public void setChannelHistory(
        @RequestParam("channelId") Long channelId,
        @RequestParam("videoId") Long videoId
    ){
        Channel c = channelRepository.getChannelById(channelId);
        c.getChannelHistory().add(videoId);
        channelRepository.save(c);
    }
    @GetMapping("/get-channel-history/{id}")
    public ResponseEntity<List<Video>> getHistoryVideos(@PathVariable("id") Long channelId){
        try{
            Channel c = channelRepository.getChannelById(channelId);
            List<Video> response = new ArrayList<>();
            Set<Long> history = c.getChannelHistory();
            for(Long id : history){
                Video v = videoRepository.getVideoById(id);
                response.add(v);
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/get-channel-by-id/{id}")
    public ResponseEntity<Channel> getChannelById(@PathVariable("id") Long channelId){
        try {
            Channel c = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(c);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @GetMapping("/get-channel-videos-by-id/{id}")
    public ResponseEntity<List<Video>> getChannelVideosById(@PathVariable("id") Long channelId){
        try {
            Channel c = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(c.getVideos());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
