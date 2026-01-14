package com.ryotube.application.Repositories;

import com.ryotube.application.Entities.Short;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShortRepository extends JpaRepository<Short, Long> {
    
    @Query("SELECT s FROM Short s WHERE s.channel.id = :channelId")
    List<Short> getAllChannelShorts(@Param("channelId") Long channelId);

    @Query("SELECT s FROM Short s ORDER BY s.uploadDateTime DESC")
    List<Short> getAllShorts();

    @Query("SELECT s FROM Short s WHERE s.id = :id")
    Short getShortById(@Param("id") Long id);

    @Query("SELECT s FROM Short s WHERE s.id != :id ORDER BY RAND()")
    List<Short> getAllShortsExceptSelf(@Param("id") Long id);

    @Query("SELECT s FROM Short s WHERE LOWER(s.title) LIKE LOWER(CONCAT(:prefix, '%'))")
    List<Short> searchByTitlePrefix(@Param("prefix") String prefix);
}
