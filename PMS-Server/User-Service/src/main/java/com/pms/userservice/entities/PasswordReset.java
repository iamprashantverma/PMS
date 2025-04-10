package com.pms.userservice.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "password_reset_tokens")
@Builder
@Data
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String otp;

    @CreationTimestamp
    private LocalDateTime createAt;

    @Column(nullable = false)
    private LocalDateTime expiryAt;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private boolean used = false;

    @Column(nullable = false)
    private String windowId;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryAt);
    }

}
