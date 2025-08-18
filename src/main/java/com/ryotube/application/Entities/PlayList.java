package com.ryotube.application.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String description;

    @JsonManagedReference
    @OneToMany(mappedBy = "playlist",cascade = CascadeType.ALL,orphanRemoval = true)
    List<Video> videos;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "channel_id")
    private Channel channel;
}
