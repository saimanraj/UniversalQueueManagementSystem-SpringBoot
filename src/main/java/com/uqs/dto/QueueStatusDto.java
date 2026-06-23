package com.uqs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QueueStatusDto {
    private Long tokenId;
    private Integer tokenNo;
    private String status;
    private Long peopleAhead;
    private Integer avgServiceTime;
    private Long etaMinutes;
    private String vendorName;
    private Long vendorId;
    private Boolean queueActive;
    private Boolean queuePaused;
    private Integer currentToken;
}
