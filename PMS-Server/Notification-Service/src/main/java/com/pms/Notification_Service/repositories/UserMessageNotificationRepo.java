package com.pms.Notification_Service.repositories;

import com.pms.Notification_Service.entities.UserMessageNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface UserMessageNotificationRepo extends JpaRepository< UserMessageNotification,Long> {


    List<UserMessageNotification> findAllByUserId(String userId);

   List<UserMessageNotification> findAllByUserIdAndIsReadFalse(String currentUserId);
}
