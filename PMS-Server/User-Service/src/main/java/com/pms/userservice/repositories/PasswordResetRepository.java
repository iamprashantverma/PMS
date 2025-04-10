package com.pms.userservice.repositories;


import com.pms.userservice.entities.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends  JpaRepository<PasswordReset,Long> {

    PasswordReset findByEmail(String email);
}
