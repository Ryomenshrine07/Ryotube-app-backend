package com.ryotube.application.Services;


import com.ryotube.application.Entities.Comment;
import com.ryotube.application.Entities.CommentReply;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.CommentRepository;
import com.ryotube.application.Repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CommentReplyService {
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    CommentRepository commentRepository;

    public void addCommentReplyToComment(Long videoId,Long commentId ,CommentReply c){
        Video v = videoRepository.getVideoById(videoId);
        Comment comment = commentRepository.getCommentById(commentId);
        if(comment.getReplies() == null){
            comment.setReplies(new ArrayList<>());
        }
        c.setLikes(0);
        c.setDisLikes(0);
        comment.getReplies().add(c);
        c.setComment(comment);
        commentRepository.save(comment);
        videoRepository.save(v);
    }
}
