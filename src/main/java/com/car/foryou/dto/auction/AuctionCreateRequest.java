package com.car.foryou.dto.auction;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuctionCreateRequest {
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\+|\\-)\\d{2}:\\d{2}$",
            message = "Invalid date format. Expected format is yyyy-MM-ddTHH:mm:ssXXX"
    )
    private String startDate;
    @Pattern(
            regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\+|\\-)\\d{2}:\\d{2}$",
            message = "Invalid date format. Expected format is yyyy-MM-ddTHH:mm:ssXXX"
    )
    private String endDate;
    private Integer dpPercent;
}
