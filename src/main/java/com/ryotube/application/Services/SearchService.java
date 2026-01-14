package com.ryotube.application.Services;

import com.ryotube.application.DTOs.*;
import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Short;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.ShortRepository;
import com.ryotube.application.Repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ShortRepository shortRepository;

    public List<String> getSuggestions(String query) {
        Set<String> suggestions = new HashSet<>();
        
        // Get video titles
        List<Video> videos = videoRepository.searchByTitlePrefix(query);
        videos.stream()
            .map(Video::getTile)
            .filter(title -> title != null && !title.isEmpty())
            .limit(5)
            .forEach(suggestions::add);
        
        // Get channel names
        List<Channel> channels = channelRepository.searchByNamePrefix(query);
        channels.stream()
            .map(Channel::getChannelName)
            .filter(name -> name != null && !name.isEmpty())
            .limit(3)
            .forEach(suggestions::add);
        
        // Get short titles
        List<Short> shorts = shortRepository.searchByTitlePrefix(query);
        shorts.stream()
            .map(Short::getTitle)
            .filter(title -> title != null && !title.isEmpty())
            .limit(3)
            .forEach(suggestions::add);
        
        // Return sorted list limited to 10 suggestions
        return suggestions.stream()
            .sorted()
            .limit(10)
            .collect(Collectors.toList());
    }

    public SearchResultDTO searchAll(String query) {
        SearchResultDTO result = new SearchResultDTO();
        result.setVideos(searchVideos(query));
        result.setChannels(searchChannels(query));
        result.setShorts(searchShorts(query));
        return result;
    }

    public List<VideoSearchDTO> searchVideos(String query) {
        List<Video> videos = videoRepository.searchByTitlePrefix(query);
        return videos.stream().map(this::mapToVideoDTO).collect(Collectors.toList());
    }

    public List<ChannelSearchDTO> searchChannels(String query) {
        List<Channel> channels = channelRepository.searchByNamePrefix(query);
        return channels.stream().map(this::mapToChannelDTO).collect(Collectors.toList());
    }

    public List<ShortSearchDTO> searchShorts(String query) {
        List<Short> shorts = shortRepository.searchByTitlePrefix(query);
        return shorts.stream().map(this::mapToShortDTO).collect(Collectors.toList());
    }

    private VideoSearchDTO mapToVideoDTO(Video video) {
        VideoSearchDTO dto = new VideoSearchDTO();
        dto.setId(video.getId());
        dto.setTitle(video.getTile());
        dto.setDescription(video.getDescription());
        dto.setThumbnail(video.getVideoThumbnail());
        dto.setDuration(video.getDuration());
        dto.setViews(video.getViews());
        dto.setLikes(video.getLikes());
        dto.setUploadDateTime(video.getUploadDateTime());
        dto.setChannelId(video.getChannelId());
        dto.setChannelName(video.getChannelName());
        if (video.getChannel() != null) {
            dto.setChannelPicUrl(video.getChannel().getChannelPicURL());
        }
        return dto;
    }

    private ChannelSearchDTO mapToChannelDTO(Channel channel) {
        ChannelSearchDTO dto = new ChannelSearchDTO();
        dto.setId(channel.getId());
        dto.setChannelName(channel.getChannelName());
        dto.setChannelDescription(channel.getChannelDescription());
        dto.setChannelPicUrl(channel.getChannelPicURL());
        dto.setSubscribersCount(channel.getSubscribersCount());
        dto.setVideoCount(channel.getVideos() != null ? channel.getVideos().size() : 0);
        return dto;
    }

    private ShortSearchDTO mapToShortDTO(Short shortVideo) {
        ShortSearchDTO dto = new ShortSearchDTO();
        dto.setId(shortVideo.getId());
        dto.setTitle(shortVideo.getTitle());
        dto.setDescription(shortVideo.getDescription());
        dto.setThumbnail(shortVideo.getShortThumbnail());
        dto.setShortUrl(shortVideo.getShortUrl());
        dto.setDuration(shortVideo.getDuration());
        dto.setViews(shortVideo.getViews());
        dto.setLikes(shortVideo.getLikes());
        dto.setUploadDateTime(shortVideo.getUploadDateTime());
        dto.setChannelId(shortVideo.getChannelId());
        dto.setChannelName(shortVideo.getChannelName());
        dto.setChannelPicUrl(shortVideo.getChannelPicUrl());
        return dto;
    }
}
