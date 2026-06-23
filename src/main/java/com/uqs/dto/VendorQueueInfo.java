package com.uqs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VendorQueueInfo {
    private Long vendorId;
    private String shopName;
    private String category;
    private Boolean isActive;
    private Boolean isPaused;
    private Integer currentToken;
    private Long waitingCount;
    private Integer avgServiceTime;
}
