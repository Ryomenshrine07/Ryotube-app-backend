package com.ryotube.application.Services;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Short;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.ShortRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShortService {

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ShortRepository shortRepository;

    public void uploadShort(Short s, Long channelId) {
        Channel channel = channelRepository.getChannelById(channelId);
        List<Short> shorts = channel.getShorts();
        if (shorts == null) {
            shorts = new ArrayList<>();
        }
        
        Short shortVideo = new Short();
        shortVideo.setShortThumbnail(s.getShortThumbnail());
        shortVideo.setShortCategory(s.getShortCategory());
        shortVideo.setTitle(s.getTitle());
        shortVideo.setViews(s.getViews());
        shortVideo.setLikes(s.getLikes());
        shortVideo.setShortUrl(s.getShortUrl());
        shortVideo.setDescription(s.getDescription());
        shortVideo.setDuration(s.getDuration());
        shortVideo.setDislikes(s.getDislikes());
        shortVideo.setCloudId(s.getCloudId());
        shortVideo.setChannel(channel);
        shortVideo.setShortChannelName(channel.getChannelName());
        
        shorts.add(shortVideo);
        channel.setShorts(shorts);
        
        shortRepository.save(shortVideo);
        channelRepository.save(channel);
    }

    public String formatDuration(Double seconds) {
        if (seconds == null) return "00:00";
        int totalSeconds = seconds.intValue();
        int minutes = totalSeconds / 60;
        int remainingSeconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, remainingSeconds);
    }
}
