package com.car.foryou.dto.bid;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BidDetailResponse {
    private int bidId;
    private int itemId;
    private String bidderName;
    private Long bidAmount;
    private ZonedDateTime bidTime;
    private BidStatus bidStatus;
}
