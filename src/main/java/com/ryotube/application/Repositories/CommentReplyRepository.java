package com.ryotube.application.Repositories;


import com.ryotube.application.Entities.CommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReplyRepository extends JpaRepository<CommentReply, Long> {

    CommentReply getCommentReplyById(Long commentId);
}
