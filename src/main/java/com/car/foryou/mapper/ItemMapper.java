package com.car.foryou.mapper;

import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.model.Item;
import com.car.foryou.model.User;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public Item mapItemRequestToItem(ItemRequest request, User user){
        return Item.builder()
                .title(request.getTitle())
                .licensePlat(request.getLicensePlate())
                .inspector(user)
                .brand(request.getBrand())
                .model(request.getModel())
                .variant(request.getVariant())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .engineCapacity(request.getEngineCapacity())
                .color(request.getColor())
                .interiorGrade(request.getInteriorGrade())
                .exteriorGrade(request.getExteriorGrade())
                .chassisGrade(request.getChassingGrade())
                .engineGrade(request.getEngineGrade())
                .build();
    }

    public ItemResponse mapToItemResponse(Item request, User user){
        return ItemResponse.builder()
                .itemId(request.getId())
                .title(request.getTitle())
                .licensePlate(request.getLicensePlat())
                .inspector(user.getUsername())
                .brand(request.getBrand())
                .model(request.getModel())
                .variant(request.getVariant())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .engineCapacity(request.getEngineCapacity())
                .mileage(request.getMileage())
                .startingPrice(request.getStartingPrice())
                .color(request.getColor())
                .auctionStart(request.getAuctionStart())
                .auctionEnd(request.getAuctionEnd())
                .status(request.getStatus())
                .interiorGrade(request.getInteriorGrade())
                .exteriorGrade(request.getExteriorGrade())
                .chassingGrade(request.getChassisGrade())
                .engineGrade(request.getEngineGrade())
                .build();
    }
}
