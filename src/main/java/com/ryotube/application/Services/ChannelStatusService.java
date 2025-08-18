package com.ryotube.application.Services;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Profile.ChannelStatus;
import com.ryotube.application.Repositories.ChannelStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ChannelStatusService {
    @Autowired
    ChannelStatusRepository channelStatusRepository;

    public ChannelStatus getChannelStatus(Channel channel){
        ChannelStatus cs = new ChannelStatus();
        cs.setCountry("India");
        cs.setJoinedDate(new Date());
        cs.setTotalViews(0L);
        cs.setChannel(channel);
        return channelStatusRepository.save(cs);
    }
}
