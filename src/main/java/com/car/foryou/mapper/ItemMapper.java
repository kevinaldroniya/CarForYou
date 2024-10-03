package com.car.foryou.mapper;

import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.model.Item;
import org.springframework.stereotype.Component;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@Component
public class ItemMapper {

    public Item mapItemRequestToItem(ItemRequest request){
        return Item.builder()
                .title(request.getTitle())
                .licensePlat(request.getLicensePlate())
                .brand(request.getBrand())
                .model(request.getModel())
                .variant(request.getVariant())
                .year(request.getYear())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .mileage(request.getMileage())
                .startingPrice(request.getStartingPrice())
                .engineCapacity(request.getEngineCapacity())
                .color(request.getColor())
                .interiorGrade(request.getInteriorGrade())
                .exteriorGrade(request.getExteriorGrade())
                .chassisGrade(request.getChassisGrade())
                .engineGrade(request.getEngineGrade())
                .build();
    }

    public ItemResponse mapToItemResponse(Item request){
        return ItemResponse.builder()
                .itemId(request.getId())
                .title(request.getTitle())
                .licensePlate(request.getLicensePlat())
                .brand(request.getBrand())
                .model(request.getModel())
                .variant(request.getVariant())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .year(request.getYear())
                .engineCapacity(request.getEngineCapacity())
                .mileage(request.getMileage())
                .startingPrice(request.getStartingPrice())
                .color(request.getColor())
                .auctionStart(request.getAuctionStart())
                .auctionEnd(request.getAuctionEnd())
                .status(ItemStatus.fromString(request.getStatus().toString()))
                .interiorGrade(request.getInteriorGrade())
                .exteriorGrade(request.getExteriorGrade())
                .chassingGrade(request.getChassisGrade())
                .engineGrade(request.getEngineGrade())
                .createdAt(ZonedDateTime.ofInstant(request.getCreatedAt(), ZoneId.systemDefault()))
                .build();
    }

}
