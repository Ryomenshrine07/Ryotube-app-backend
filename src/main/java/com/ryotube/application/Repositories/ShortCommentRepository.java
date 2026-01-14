package com.ryotube.application.Repositories;

import com.ryotube.application.Entities.ShortComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShortCommentRepository extends JpaRepository<ShortComment, Long> {
    
    @Query("SELECT c FROM ShortComment c WHERE c.shortVideo.id = :shortId ORDER BY c.timestamp DESC")
    List<ShortComment> getCommentsByShortId(@Param("shortId") Long shortId);
    
    @Query("SELECT c FROM ShortComment c WHERE c.id = :id")
    ShortComment getCommentById(@Param("id") Long id);
}
