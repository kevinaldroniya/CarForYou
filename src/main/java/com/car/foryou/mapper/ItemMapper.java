package com.car.foryou.mapper;

import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.model.Item;
import org.springframework.stereotype.Component;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
                .interiorItemGrade(request.getInteriorItemGrade())
                .exteriorItemGrade(request.getExteriorItemGrade())
                .chassisItemGrade(request.getChassisItemGrade())
                .engineItemGrade(request.getEngineItemGrade())
                .build();
    }

    public ItemResponse mapToItemResponse(Item request){
        String auctioneer = request.getAuctioneer() != null ? request.getAuctioneer().getUsername() : null;
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
                .interiorItemGrade(request.getInteriorItemGrade())
                .exteriorItemGrade(request.getExteriorItemGrade())
                .chassingItemGrade(request.getChassisItemGrade())
                .engineItemGrade(request.getEngineItemGrade())
                .createdAt(ZonedDateTime.ofInstant(request.getCreatedAt(), ZoneId.systemDefault()))
                .auctioneer(auctioneer)
                .build();
    }

}
