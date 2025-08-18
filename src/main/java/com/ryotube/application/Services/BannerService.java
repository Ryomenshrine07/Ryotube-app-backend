package com.ryotube.application.Services;

import com.ryotube.application.Entities.Channel;
import com.ryotube.application.Profile.Banner;
import com.ryotube.application.Repositories.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BannerService {
    @Autowired
    BannerRepository bannerRepository;
    public Banner getBanner(Channel channel){
        Banner b = new Banner();
        b.setBannerHead("This is your banner heading");
        b.setBannerDescription("This is your banner description");
        b.setBannerPicUrl("");
        b.setChannel(channel);
        return bannerRepository.save(b);
    }
}
