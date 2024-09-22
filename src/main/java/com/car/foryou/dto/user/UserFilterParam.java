package com.car.foryou.dto.user;

import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class UserFilterParam {
    private String username = "";
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "id";
    @Pattern(regexp = "ASC|DESC", message = "sortDirection must be 'ASC' or 'DESC'")
    private String sortingDirection = "ASC";

}
