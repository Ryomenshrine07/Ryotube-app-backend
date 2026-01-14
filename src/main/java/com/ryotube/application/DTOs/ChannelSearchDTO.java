package com.ryotube.application.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelSearchDTO {
    private Long id;
    private String channelName;
    private String channelDescription;
    private String channelPicUrl;
    private Long subscribersCount;
    private int videoCount;
}
