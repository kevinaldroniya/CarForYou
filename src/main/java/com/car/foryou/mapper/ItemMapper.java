package com.car.foryou.mapper;

import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.item.ItemStatus;
import com.car.foryou.model.Item;
import com.car.foryou.model.User;
import com.car.foryou.model.Variant;
import org.springframework.stereotype.Component;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class ItemMapper {

    public Item mapItemRequestToItem(ItemRequest request, User user, Variant variant){
        return Item.builder()
                .title(request.getTitle())
                .licensePlat(request.getLicensePlate())
                .inspector(user)
                .variant(variant)
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .mileage(request.getMileage())
                .startingPrice(request.getStartingPrice())
                .engineCapacity(request.getEngineCapacity())
                .color(request.getColor())
                .interiorGrade(request.getInteriorGrade())
                .exteriorGrade(request.getExteriorGrade())
                .chassisGrade(request.getChassingGrade())
                .engineGrade(request.getEngineGrade())
                .status(ItemStatus.AVAILABLE)
                .build();
    }

    public ItemResponse mapToItemResponse(Item request){
        return ItemResponse.builder()
                .itemId(request.getId())
                .title(request.getTitle())
                .licensePlate(request.getLicensePlat())
                .inspector(request.getInspector().getUsername())
                .brand(request.getVariant().getCarModel().getBrand().getName())
                .model(request.getVariant().getCarModel().getName())
                .variant(request.getVariant().getName())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .year(request.getVariant().getYear())
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
