package com.ryotube.application.Profile;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ryotube.application.Entities.Channel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "channelStatus")
public class ChannelStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private Date joinedDate;
    private long totalViews;
    private String country;

    @JsonBackReference
    @OneToOne(mappedBy = "channelStatus")
    private Channel channel;
}
