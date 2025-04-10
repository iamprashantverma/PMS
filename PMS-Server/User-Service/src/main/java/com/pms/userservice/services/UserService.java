package com.pms.userservice.services;

import com.pms.userservice.dto.ResponseDTO;
import com.pms.userservice.dto.UserDTO;
import com.pms.userservice.entities.User;
import com.pms.userservice.entities.enums.Roles;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {

    /**
     * Retrieves a user entity by user ID.
     *
     * @param userId the ID of the user
     * @return the User entity
     */
    User getUserById(String userId);

    /**
     * Finds a user by their email address.
     *
     * @param userEmail the email of the user
     * @return the User entity
     */
    User findUserByEmail(String userEmail);

    /**
     * Assigns a specific role to a user.
     *
     * @param userId the ID of the user
     * @param role   the role to be assigned
     * @return a response containing success or failure message
     */
    @Transactional
    ResponseDTO assignRoleToUser(String userId, Roles role);

    /**
     * Retrieves detailed information for a specific user.
     *
     * @param userId the ID of the user
     * @return the user details in DTO form
     */
    UserDTO getUserDetails(String userId);

    /**
     * Retrieves a list of all users in the system.
     *
     * @return a list of UserDTOs
     */
    List<UserDTO> getAllUsers();

    /**
     * Updates the details of a user, optionally with a profile image.
     *
     * @param userDTO the new user details
     * @param file    the user's profile picture (optional)
     * @return a response containing success or failure message
     */
    @Transactional
    ResponseDTO updateUserDetails(UserDTO userDTO, MultipartFile file);

    /**
     * Deactivates a user account by setting its status to inactive.
     *
     * @param userId the ID of the user to deactivate
     * @return a response containing success or failure message
     */
    @Transactional
    ResponseDTO deactivateUser(String userId);

    /**
     * Updates the user-specific notification settings.
     *
     * @param userId      the ID of the user
     * @param taskUpdates the type of notification setting to update
     * @param value       the new value (true or false)
     * @return the updated user DTO
     */
    UserDTO updateNotificationField(String userId, String taskUpdates, Boolean value);
}
