package com.ryotube.application.Repositories;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    @Query("SELECT c from Channel c where c.user.email=:email")
    Channel getChannelByUserEmail(String email);

    @Query("SELECT c from Channel c where c.id=:channelId")
    Channel getChannelById(Long channelId);

}
