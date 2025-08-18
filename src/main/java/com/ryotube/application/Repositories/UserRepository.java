package com.ryotube.application.Repositories;

import com.ryotube.application.Entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT u from User u where u.email=:email")
    public User getUserByEmail(String email);

    User getById(Long userId);
}
