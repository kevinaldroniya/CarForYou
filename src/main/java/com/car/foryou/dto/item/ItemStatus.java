package com.car.foryou.dto.item;

import lombok.Getter;

@Getter
public enum ItemStatus {
    AVAILABLE("available"),
    SOLD("sold"),
    RESERVED("reserved"),
    REMOVED("removed"),
    AUCTION_SCHEDULED("auction_scheduled"),
    ON_AUCTION("on_auction"),
    AUCTION_ENDED("auction_ended"),
    DRAFT("draft");

    private final String value;

    ItemStatus(String value){
        this.value = value;
    }

    public static ItemStatus fromString(String text){
        for (ItemStatus status : ItemStatus.values()){
            if (status.value.equalsIgnoreCase(text)){
                return status;
            }
        }
        throw new IllegalArgumentException("No constant found with value : " + text);
    }

    @Override
    public String toString(){
        return value;
    }
}
