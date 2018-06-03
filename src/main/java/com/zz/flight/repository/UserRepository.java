package com.zz.flight.repository;

import com.zz.flight.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserName(String username);

    User findByEmail(String email);

    User findByPhone(String phone);
}
