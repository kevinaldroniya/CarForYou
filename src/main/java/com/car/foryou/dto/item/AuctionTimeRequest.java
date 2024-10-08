package com.car.foryou.dto.item;

import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionTimeRequest {
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\+|\\-)\\d{2}:\\d{2}$",
            message = "Invalid date format. Expected format is yyyy-MM-ddTHH:mm:ssXXX"
    )
    private String auctionStartTime;
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\+|\\-)\\d{2}:\\d{2}$",
            message = "Invalid date format. Expected format is yyyy-MM-ddTHH:mm:ssXXX"
    )
    private String auctionEndTime;
}
