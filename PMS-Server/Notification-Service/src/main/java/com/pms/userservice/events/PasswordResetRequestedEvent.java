package com.pms.userservice.events;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequestedEvent {
    private Long id;
    private String userId;
    private String email;
    private String name;
    private String otp;
    private LocalDateTime requestedAt;

}
