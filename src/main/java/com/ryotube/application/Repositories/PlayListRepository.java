package com.ryotube.application.Repositories;

import com.ryotube.application.Entities.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayListRepository extends JpaRepository<PlayList, Long> {
    
    List<PlayList> findByChannel_Id(Long channelId);
    
    List<PlayList> findByChannel_IdAndIsPublicTrue(Long channelId);
    
    @Query("SELECT p FROM PlayList p WHERE p.channel.id = :channelId AND p.name LIKE %:name%")
    List<PlayList> findByChannelIdAndNameContaining(@Param("channelId") Long channelId, @Param("name") String name);
    
    @Query("SELECT p FROM PlayList p JOIN p.videos v WHERE v.id = :videoId")
    List<PlayList> findPlaylistsContainingVideo(@Param("videoId") Long videoId);
    
    @Query("SELECT COUNT(v) FROM PlayList p JOIN p.videos v WHERE p.id = :playlistId")
    Long countVideosByPlaylistId(@Param("playlistId") Long playlistId);
}