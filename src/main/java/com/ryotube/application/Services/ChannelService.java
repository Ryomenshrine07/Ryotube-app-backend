package com.ryotube.application.Services;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.User;
import com.ryotube.application.Profile.Banner;
import com.ryotube.application.Profile.ChannelStatus;
import com.ryotube.application.Repositories.BannerRepository;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.ChannelStatusRepository;
import com.ryotube.application.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChannelService {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    BannerService bannerService;
    @Autowired
    ChannelStatusService channelStatusService;
    @Autowired
    BannerRepository bannerRepository;
    @Autowired
    ChannelStatusRepository channelStatusRepository;

    @Transactional
    public Channel createChannel(User u){
        Channel c = channelRepository.getChannelByUserEmail(u.getEmail());
        if(c != null) return c;
        Channel channel = new Channel();
        channel.setChannelName(u.getUsername()+" 's Channel");
        channel.setChannelDescription("This is your channel description");
        channel.setGitHubLink("None");
        channel.setTwitterLink("None");
        channel.setWebsiteLink("None");
        channel.setSubscribersCount(0L);

        Banner banner = bannerService.getBanner(channel);
        ChannelStatus channelStatus = channelStatusService.getChannelStatus(channel);
        if(banner != null){
            channel.setBanner(banner);
        }
        if(channelStatus != null){
            channel.setChannelStatus(channelStatus);
        }
        channel.setUser(u);
        u.setChannel(channel);
        System.out.println("R");
        return channelRepository.save(channel);
    }
}
