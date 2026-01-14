package com.ryotube.application.Services;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.PlayList;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.PlayListRepository;
import com.ryotube.application.Repositories.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlayListService {

    @Autowired
    private PlayListRepository playListRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private ChannelRepository channelRepository;

    public PlayList createPlaylist(Long channelId, String name, String description, boolean isPublic) {
        Optional<Channel> channelOpt = channelRepository.findById(channelId);
        if (channelOpt.isEmpty()) {
            throw new RuntimeException("Channel not found");
        }

        PlayList playlist = new PlayList();
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setPublic(isPublic);
        playlist.setChannel(channelOpt.get());

        return playListRepository.save(playlist);
    }

    public PlayList addVideoToPlaylist(Long playlistId, Long videoId) {
        Optional<PlayList> playlistOpt = playListRepository.findById(playlistId);
        Optional<Video> videoOpt = videoRepository.findById(videoId);

        if (playlistOpt.isEmpty()) {
            throw new RuntimeException("Playlist not found");
        }
        if (videoOpt.isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        PlayList playlist = playlistOpt.get();
        Video video = videoOpt.get();

        if (!playlist.getVideos().contains(video)) {
            playlist.getVideos().add(video);
            video.getPlaylists().add(playlist);
        }

        return playListRepository.save(playlist);
    }

    public PlayList removeVideoFromPlaylist(Long playlistId, Long videoId) {
        Optional<PlayList> playlistOpt = playListRepository.findById(playlistId);
        Optional<Video> videoOpt = videoRepository.findById(videoId);

        if (playlistOpt.isEmpty()) {
            throw new RuntimeException("Playlist not found");
        }
        if (videoOpt.isEmpty()) {
            throw new RuntimeException("Video not found");
        }

        PlayList playlist = playlistOpt.get();
        Video video = videoOpt.get();

        playlist.getVideos().remove(video);
        video.getPlaylists().remove(playlist);

        return playListRepository.save(playlist);
    }

    public List<PlayList> getChannelPlaylists(Long channelId) {
        return playListRepository.findByChannel_Id(channelId);
    }

    public List<PlayList> getPublicChannelPlaylists(Long channelId) {
        return playListRepository.findByChannel_IdAndIsPublicTrue(channelId);
    }

    public Optional<PlayList> getPlaylistById(Long playlistId) {
        return playListRepository.findById(playlistId);
    }

    public void deletePlaylist(Long playlistId) {
        Optional<PlayList> playlistOpt = playListRepository.findById(playlistId);
        if (playlistOpt.isPresent()) {
            PlayList playlist = playlistOpt.get();
            // Remove associations with videos
            for (Video video : playlist.getVideos()) {
                video.getPlaylists().remove(playlist);
            }
            playlist.getVideos().clear();
            playListRepository.delete(playlist);
        }
    }

    public PlayList updatePlaylist(Long playlistId, String name, String description, Boolean isPublic) {
        Optional<PlayList> playlistOpt = playListRepository.findById(playlistId);
        if (playlistOpt.isEmpty()) {
            throw new RuntimeException("Playlist not found");
        }

        PlayList playlist = playlistOpt.get();
        if (name != null) playlist.setName(name);
        if (description != null) playlist.setDescription(description);
        if (isPublic != null) playlist.setPublic(isPublic);

        return playListRepository.save(playlist);
    }
}