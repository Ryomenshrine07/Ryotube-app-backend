package com.ryotube.application.Repositories;

import com.ryotube.application.Entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video,Long> {
    @Query("SELECT v FROM Video v WHERE v.channel.id = :channelId")
    List<Video> getAllChannelVideos(@Param("channelId") Long channelId);

    @Query("SELECT v FROM Video v")
    List<Video> getAllVideos();

    @Query("SELECT v from Video v where v.id = :id")
    Video getVideoById(Long id);

    @Query("Select v from Video v where v.id != :id")
    List<Video> getAllVideosExceptSelf(@Param("id") Long id);

    @Query("SELECT v from Video v where v.channel.id = :id")
    List<Video> getAllVideosOfId(Long id);

}
