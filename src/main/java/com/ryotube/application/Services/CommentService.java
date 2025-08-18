package com.ryotube.application.Services;


import com.ryotube.application.Entities.Comment;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.CommentRepository;
import com.ryotube.application.Repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CommentService {
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    CommentRepository commentRepository;

    public void addCommentToVideo(Long videoId, Comment c){
        Video v = videoRepository.getVideoById(videoId);
        if(v.getComments() == null){
            v.setComments(new ArrayList<>());
        }
        c.setLikes(0);
        c.setDisLikes(0);
        c.setVideo(v);
        v.getComments().add(c);
        commentRepository.save(c);
        videoRepository.save(v);
    }
}
