package com.pms.Notification_Service.repositories;

import com.pms.Notification_Service.entities.UserDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDetails,String> {
}
