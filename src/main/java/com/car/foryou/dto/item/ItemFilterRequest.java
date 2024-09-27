package com.car.foryou.dto.item;

import com.car.foryou.dto.FilterParam;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ItemFilterRequest extends FilterParam {
    private String query = "";

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemFilterRequest that = (ItemFilterRequest) o;
        return Objects.equals(query, that.query);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(query);
    }
}
