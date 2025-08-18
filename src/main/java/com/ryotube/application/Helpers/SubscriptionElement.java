package com.ryotube.application.Helpers;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionElement {
    Long channelId;
    String channelPicURL;
    String channelName;
    Long subscriberCount;
    Long videosCount;
}
