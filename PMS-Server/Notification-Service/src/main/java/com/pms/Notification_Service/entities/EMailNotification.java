package com.pms.Notification_Service.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class EMailNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
