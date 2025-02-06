package com.pms.userservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
public class Session {

    @Id
    private Long id;

    @ManyToOne
    private User user;

    private String refreshToken;

    @CreationTimestamp
    private LocalDateTime lastUsedBy;


}
