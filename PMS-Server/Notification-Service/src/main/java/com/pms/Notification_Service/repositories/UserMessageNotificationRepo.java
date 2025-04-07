package com.pms.Notification_Service.repositories;

import com.pms.Notification_Service.entities.UserMessageNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMessageNotificationRepo extends JpaRepository< UserMessageNotification,Long> {
}
