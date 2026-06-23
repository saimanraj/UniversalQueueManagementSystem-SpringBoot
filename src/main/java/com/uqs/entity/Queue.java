package com.uqs.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Queue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vendor_id", nullable = false, unique = true)
    private Vendor vendor;

    @Column(name = "current_token")
    private Integer currentToken = 0;

    @Column(name = "is_active")
    private Boolean isActive = false;

    @Column(name = "is_paused")
    private Boolean isPaused = false;

    @Column(name = "opened_at")
    private LocalDateTime openedAt;

    @Column(name = "last_called_at")
    private LocalDateTime lastCalledAt;
}
