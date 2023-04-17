package com.edoyou.k2sbeauty.entities.repositories;


import com.edoyou.k2sbeauty.entities.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository {
  User findByEmail(String email);
}
