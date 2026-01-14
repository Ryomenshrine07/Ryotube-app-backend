package com.ryotube.application.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {
    private List<VideoSearchDTO> videos;
    private List<ChannelSearchDTO> channels;
    private List<ShortSearchDTO> shorts;
}
