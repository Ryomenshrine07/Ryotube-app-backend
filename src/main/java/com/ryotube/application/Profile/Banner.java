package com.ryotube.application.Profile;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ryotube.application.Entities.Channel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String bannerHead;
    private String bannerDescription;
    private String bannerPicUrl;
    

    @JsonBackReference
    @OneToOne(mappedBy = "banner")
    private Channel channel;
}
