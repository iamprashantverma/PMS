package com.pms.userservice.events;

import lombok.*;

import java.time.LocalDateTime;
@Getter
@Setter
@RequiredArgsConstructor
@Builder
public class PasswordResetRequestedEvent {
    private String userId;
    private String email;
    private String name;
    private String otp;
    private LocalDateTime requestedAt;

}
