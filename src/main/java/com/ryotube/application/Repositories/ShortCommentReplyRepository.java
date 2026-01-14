package com.ryotube.application.Repositories;

import com.ryotube.application.Entities.ShortCommentReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShortCommentReplyRepository extends JpaRepository<ShortCommentReply, Long> {
    
    @Query("SELECT r FROM ShortCommentReply r WHERE r.id = :id")
    ShortCommentReply getReplyById(@Param("id") Long id);
}
