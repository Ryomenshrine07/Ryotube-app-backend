package com.ryotube.application.Services;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Entities.Video;
import com.ryotube.application.Repositories.ChannelRepository;
import com.ryotube.application.Repositories.VideoRepository;
import org.mp4parser.IsoFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoService {

    @Autowired
    ChannelRepository channelRepository;

    @Autowired
    VideoRepository videoRepository;
;

    public void uploadVideo(Video v, Long channelId) {
        Channel channel = channelRepository.getChannelById(channelId);
        List<Video> videos = channel.getVideos();
        if(videos == null){
            videos = new ArrayList<>();
        }
        Video video = new Video();
        video.setVideoThumbnail(v.getVideoThumbnail());
        video.setVideoCategory(v.getVideoCategory());
        video.setTile(v.getTile());
        video.setViews(v.getViews());
        video.setLikes(v.getLikes());
        video.setVideoUrl(v.getVideoUrl());
        video.setDescription(v.getDescription());
        video.setDuration(v.getDuration());
        video.setDislikes(v.getDislikes());
        video.setChannel(channel);
        videos.add(video);
        video.setVideoChannelName(channel.getChannelName());
        channel.setVideos(videos);
        videoRepository.save(video);
        channelRepository.save(channel);
    }
    public String getVideoDuration(MultipartFile file) throws IOException {
        // Save MultipartFile to a temp file
        File convFile = File.createTempFile("upload", ".mp4");
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }

        // Read MP4 duration
        IsoFile isoFile = new IsoFile(convFile.getAbsolutePath());
        double lengthInSeconds =
                (double) isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                        isoFile.getMovieBox().getMovieHeaderBox().getTimescale();

        isoFile.close();
        // Format mm:ss
        int minutes = (int) (lengthInSeconds / 60);
        int seconds = (int) (lengthInSeconds % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }
}
