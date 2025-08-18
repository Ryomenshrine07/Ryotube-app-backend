package com.ryotube.application.Repositories;

import com.ryotube.application.Profile.ChannelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelStatusRepository extends JpaRepository<ChannelStatus, Long> {
}
