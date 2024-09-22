package com.car.foryou.dto.group;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GroupResponse {
    private long id;
    private String name;
}
