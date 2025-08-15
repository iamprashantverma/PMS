package com.pms.Notification_Service.service;

import com.pms.userservice.events.UserSignupEvent;

/**
 * Service interface for managing user-related operations in the Notification Service.
 * This service handles storing, updating, and removing user details
 * based on events received from other microservices.
 */
public interface UserService {

    /**
     * Persist new user details in the notification service's data store.
     *
     * @param event the {@link UserSignupEvent} containing the new user's details
     */
    void saveUserDetails(UserSignupEvent event);

    /**
     * Update existing user details in the notification service's data store.
     *
     * @param event the {@link UserSignupEvent} containing the updated user's details
     */
    void updateUserDetails(UserSignupEvent event);

    /**
     * Remove a user's details from the notification service's data store.
     *
     * @param event the {@link UserSignupEvent} containing the user's identification details
     */
    void removeUserDetails(UserSignupEvent event);
}
