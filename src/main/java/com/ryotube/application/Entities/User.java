package com.ryotube.application.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "users")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;
    private String username;
    private String password;
    // TODO: Add user profile picture in future

    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "channel_id",referencedColumnName = "id")
    private Channel channel;
}
