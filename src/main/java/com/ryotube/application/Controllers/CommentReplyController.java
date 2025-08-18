package com.ryotube.application.Controllers;


import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Comment;
import com.ryotube.application.Entities.CommentReply;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.CommentReplyRepository;
import com.ryotube.application.Repositories.CommentRepository;
import com.ryotube.application.Repositories.VideoRepository;
import com.ryotube.application.Services.CommentReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentReplyController {
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    CommentReplyRepository commentReplyRepository;
    @Autowired
    CommentReplyService commentReplyService;

    @PostMapping("/post-comment-reply")
    public void postComment(
            @RequestParam("videoId") Long videoId,
            @RequestParam("username")String username,
            @RequestParam("userPicURL") String userPicURL,
            @RequestParam("userChannelId") Long userChannelId,
            @RequestParam("commentData") String commentData,
            @RequestParam("commentId") Long commentId
    ) {
        CommentReply cR = new CommentReply();
        cR.setUserChannelId(userChannelId);
        cR.setCommentData(commentData);
        cR.setUsername(username);
        cR.setUserPicURL(userPicURL);
        commentReplyService.addCommentReplyToComment(videoId,commentId,cR);
    }
    @GetMapping("/load-comments-reply/{id}")
    public ResponseEntity<List<CommentReply>> getVideoComments(@PathVariable("id") Long commentId){
        try{
            Comment c = commentRepository.getCommentById(commentId);
            return ResponseEntity.ok(c.getReplies());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/delete-comment-reply")
    public void deleteVideoComment(
            @RequestParam("commentId") Long commentId,
            @RequestParam("commentReplyId") Long commentReplyId
    ){
        Comment comment = commentRepository.getCommentById(commentId);
        for(CommentReply reply : comment.getReplies()){
            if(reply.getId() == commentReplyId){
                comment.getReplies().remove(reply);
                break;
            }
        }
        commentRepository.save(comment);
    }
    @PostMapping("/check-comment-reply-liked")
    public ResponseEntity<Boolean> checkIsCommentReplyLiked(
            @RequestParam("channelId") Long channelId,
            @RequestParam("commentReplyId") Long commentReplyId
    ){
        try{
            Channel channel = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(channel.getLikedCommentReplies().contains(commentReplyId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/check-comment-reply-disliked")
    public ResponseEntity<Boolean> checkIsCommentReplyDisliked(
            @RequestParam("channelId") Long channelId,
            @RequestParam("commentReplyId") Long commentReplyId
    ){
        try{
            Channel channel = channelRepository.getChannelById(channelId);
            return ResponseEntity.ok(channel.getDislikedCommentReplies().contains(commentReplyId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    @PostMapping("/increase-comment-reply-like")
    public void increaseCommentReplyLike(
            @RequestParam("channelId") Long userChannelId,
            @RequestParam("commentReplyId") Long commentId
    ){
//        System.out.println("I WAS HERERERERE");
        Channel userChannel = channelRepository.getChannelById(userChannelId);
        CommentReply commentReply = commentReplyRepository.getCommentReplyById(commentId);
        if(userChannel.getDislikedCommentReplies().contains(commentId)){
            userChannel.getDislikedCommentReplies().remove(commentId);
            commentReply.setDisLikes(commentReply.getDisLikes() - 1);
        }
        userChannel.getLikedCommentReplies().add(commentId);
        commentReply.setLikes(commentReply.getLikes() + 1);
        commentReplyRepository.save(commentReply);
        channelRepository.save(userChannel);
    }
    @PostMapping("/decrease-comment-reply-like")
    public void decreaseCommentReplyLike(
            @RequestParam("channelId") Long userChannelId,
            @RequestParam("commentReplyId") Long commentId
    ){
        Channel userChannel = channelRepository.getChannelById(userChannelId);
        CommentReply commentReply = commentReplyRepository.getCommentReplyById(commentId);
        if(userChannel.getLikedCommentReplies().contains(commentId)){
            userChannel.getLikedCommentReplies().remove(commentId);
            commentReply.setLikes(commentReply.getLikes() - 1);
            commentReplyRepository.save(commentReply);
            channelRepository.save(userChannel);
        }
    }

    @PostMapping("/increase-comment-reply-dislike")
    public void increaseCommentReplyDisLike(
            @RequestParam("channelId") Long userChannelId,
            @RequestParam("commentReplyId") Long commentId
    ){
//        System.out.println("I WAS HERERERERE");
        Channel userChannel = channelRepository.getChannelById(userChannelId);
        CommentReply commentReply = commentReplyRepository.getCommentReplyById(commentId);
        if(userChannel.getLikedCommentReplies().contains(commentId)){
            userChannel.getLikedCommentReplies().remove(commentId);
            commentReply.setLikes(commentReply.getDisLikes() - 1);
        }
        userChannel.getDislikedCommentReplies().add(commentId);
        commentReply.setDisLikes(commentReply.getDisLikes() + 1);
        commentReplyRepository.save(commentReply);
        channelRepository.save(userChannel);
    }

    @PostMapping("/decrease-comment-reply-dislike")
    public void decreaseCommentDisLike(
            @RequestParam("channelId") Long userChannelId,
            @RequestParam("commentReplyId") Long commentId
    ){
        Channel userChannel = channelRepository.getChannelById(userChannelId);
        CommentReply commentReply = commentReplyRepository.getCommentReplyById(commentId);
        if(userChannel.getDislikedCommentReplies().contains(commentId)){
            userChannel.getDislikedCommentReplies().remove(commentId);
            commentReply.setDisLikes(commentReply.getDisLikes() - 1);
            commentReplyRepository.save(commentReply);
            channelRepository.save(userChannel);
        }
    }
}
