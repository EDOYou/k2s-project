package com.edoyou.k2sbeauty.repositories;

import com.edoyou.k2sbeauty.entities.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

  Client findByEmail(String email);
}