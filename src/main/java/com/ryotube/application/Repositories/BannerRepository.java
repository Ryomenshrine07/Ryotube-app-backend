package com.ryotube.application.Repositories;

import com.ryotube.application.Profile.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

}
