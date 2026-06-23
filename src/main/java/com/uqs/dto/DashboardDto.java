package com.uqs.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DashboardDto {
    private long totalUsers;
    private long totalVendors;
    private long pendingVendors;
    private long activeQueues;
    private long totalTokensToday;
}
