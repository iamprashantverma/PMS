package com.pms.userservice.services;

import com.pms.userservice.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService  extends UserDetailsService {

    User getUserById(String userId);

}
