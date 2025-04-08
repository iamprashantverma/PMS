package com.pms.userservice.services;

import com.pms.userservice.dto.ResponseDTO;
import com.pms.userservice.dto.UserDTO;
import com.pms.userservice.entities.User;
import com.pms.userservice.entities.enums.Roles;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    /* get the user by their id */
    User getUserById(String userId);

    /* get user by email */
    User findUserByEmail(String userEmail);

    /* get all roles */
    List<Roles> getAllRoles();

    /* assigning the specific role to the user*/
    @Transactional
    ResponseDTO assignRoleToUser(String userId, Roles role) ;

    /* get User Details */
    UserDTO getUserDetails(String userId) ;

    /*get all users */
    List<UserDTO> getAllUsers();

    /*update the user details */
    @Transactional
    ResponseDTO updateUserDetails(UserDTO userDTO, MultipartFile file);

    /*deactivate the user */
    @Transactional
    ResponseDTO deactivateUser(String userId);


    UserDTO updateNotificationField(String userId, String taskUpdates, Boolean value);
}
