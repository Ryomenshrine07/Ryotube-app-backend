package com.ryotube.application.Controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Comment;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.CommentRepository;
import com.ryotube.application.Repositories.VideoRepository;
import com.ryotube.application.Services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentService commentService;

    @PostMapping("/post-comment")
    public void postComment(
            @RequestParam("videoId") Long videoId,
            @RequestParam("username")String username,
            @RequestParam("userPicURL") String userPicURL,
            @RequestParam("userChannelId") Long userChannelId,
            @RequestParam("commentData") String commentData
            ) {
        Comment c = new Comment();
        c.setUserChannelId(userChannelId);
        c.setCommentData(commentData);
        c.setUsername(username);
        c.setUserPicURL(userPicURL);
        commentService.addCommentToVideo(videoId,c);
    }
    @GetMapping("/load-comments/{id}")
    public ResponseEntity<List<Comment>> getVideoComments(@PathVariable("id") Long videoId){
        try{
//            Video v = videoRepository.getVideoById(videoId);
            List<Comment> comments = commentRepository.findRepliesByVideoIdOrderByTimestampDesc(videoId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/delete-comment")
    public void deleteVideoComment(
        @RequestParam("videoId") Long videoId,
        @RequestParam("commentId") Long commentId
    ){
        Video v = videoRepository.getVideoById(videoId);
        for(Comment c : v.getComments()){
            if(c.getId() == commentId){
                v.getComments().remove(c);
                break;
            }
        }
        videoRepository.save(v);
    }
    @PostMapping("/increase-comment-like")
    public void increaseCommentLike(
            @RequestParam("channelId") Long userChannelId,
            @RequestParam("commentId") Long commentId
    ){
//        System.out.println("I WAS HERERERERE");
        Channel userChannel = channelRepository.getChannelById(userChannelId);
        Comment comment = commentRepository.getCommentById(commentId);
        if(userChannel.getDislikedComments().contains(commentId)){
            userChannel.getDislikedComments().remove(commentId);
            comment.setDisLikes(comment.getDisLikes() - 1);
        }
        userChannel.getLikedComments().add(commentId);
        comment.setLikes(comment.getLikes() + 1);
        commentRepository.save(comment);
        channelRepository.save(userChannel);
    }
    @PostMapping("/decrease-comment-like")
    public void decreaseCommentLike(
            @RequestParam("channelId") Long userChannelId,
            @RequestParam("commentId") Long commentId
    ){
        Channel userChannel = channelRepository.getChannelById(userChannelId);
        Comment comment = commentRepository.getCommentById(commentId);
        if(userChannel.getLikedComments().contains(commentId)){
            userChannel.getLikedComments().remove(commentId);
            comment.setLikes(comment.getLikes() - 1);
            commentRepository.save(comment);
            channelRepository.save(userChannel);
        }
    }
    @PostMapping("/increase-comment-dislike")
    public void increaseCommentDislike(
            @RequestParam("channelId") Long userChannelId,
            @RequestParam("commentId") Long commentId
    ){
        Channel userChannel = channelRepository.getChannelById(userChannelId);
        Comment comment = commentRepository.getCommentById(commentId);
        if(userChannel.getLikedComments().contains(commentId)){
            userChannel.getLikedComments().remove(commentId);
            comment.setLikes(comment.getLikes() - 1);
        }
        userChannel.getDislikedComments().add(commentId);
        comment.setDisLikes(comment.getDisLikes() + 1);
        commentRepository.save(comment);
        channelRepository.save(userChannel);
    }

    @PostMapping("/decrease-comment-dislike")
    public void decreaseCommentDislike(
            @RequestParam("channelId") Long userChannelId,
            @RequestParam("commentId") Long commentId
    ){
        Channel userChannel = channelRepository.getChannelById(userChannelId);
        Comment comment = commentRepository.getCommentById(commentId);
        if(userChannel.getDislikedComments().contains(commentId)){
            userChannel.getDislikedComments().remove(commentId);
            comment.setDisLikes(comment.getDisLikes() - 1);
            commentRepository.save(comment);
            channelRepository.save(userChannel);
        }
    }

    @PostMapping("/check-comment-liked")
    public ResponseEntity<Boolean> checkIsCommentLiked(
            @RequestParam("channelId") Long channelId,
            @RequestParam("commentId") Long commentId
    ){
        try{
            Channel channel = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(channel.getLikedComments().contains(commentId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/check-comment-disliked")
    public ResponseEntity<Boolean> checkIsCommentDisliked(
            @RequestParam("channelId") Long channelId,
            @RequestParam("commentId") Long commentId
    ){
        try{
            Channel channel = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(channel.getDislikedComments().contains(commentId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
