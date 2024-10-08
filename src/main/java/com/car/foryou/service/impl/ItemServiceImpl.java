package com.car.foryou.service.impl;

import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.dto.item.*;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.dto.variant.VariantCriteria;
import com.car.foryou.exception.ConversionException;
import com.car.foryou.exception.GeneralException;
import com.car.foryou.exception.InvalidRequestException;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.ItemMapper;
import com.car.foryou.mapper.VariantMapper;
import com.car.foryou.model.*;
import com.car.foryou.repository.*;
import com.car.foryou.service.BrandService;
import com.car.foryou.service.CarModelService;
import com.car.foryou.service.ItemService;
import com.car.foryou.service.VariantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final BrandService brandService;
    private final CarModelService modelService;
    private final VariantService variantService;
    private final VariantMapper variantMapper;


    @Override
    public Page<ItemResponse> getAllItems(ItemFilterRequest filterRequest) {
        Sort sort = filterRequest.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(filterRequest.getSortBy()).ascending() : Sort.by(filterRequest.getSortBy()).descending();
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        Page<Item> itemPage = itemRepository.findAll(ItemSpecifications.hasAnyKeywords(filterRequest), pageable);
        return itemPage.map(itemMapper::mapToItemResponse);
    }

    @Override
    public ItemResponse getItemById(int id) {
        Item item = itemRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Item", "ID", id)
        );
        return itemMapper.mapToItemResponse(item);
    }


    @Transactional
    @Override
    public ItemResponse createItem(ItemRequest request) {
       try {
           validateItemRequest(request);
           Item item = itemMapper.mapItemRequestToItem(request);
           item.setStatus(ItemStatus.AVAILABLE);
           Item saved = itemRepository.save(item);
           return itemMapper.mapToItemResponse(saved);
       }catch (ResourceNotFoundException e){
           throw new GeneralException(e.getMessage(), HttpStatus.NOT_FOUND);
       }
    }

    @Transactional
    @Override
    public ItemResponse updateItem(Integer id, ItemRequest request) {
        try {
            Item item = itemRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException("Item", "ID", id)
            );
            validateItemRequest(request);
            Item updated = updateItem(item, request);
            Item saved = itemRepository.save(updated);
            return itemMapper.mapToItemResponse(saved);
        }catch (ResourceNotFoundException e){
            throw new GeneralException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ItemResponse updateAuctionInfo(Long id, AuctionDetailRequest request) {

        return null;
    }

    @Override
    public ItemResponse deleteItem(Long id) {
        return null;
    }

    @Override
    public ItemResponse updateItemAuctionTime(Integer id, AuctionTimeRequest request) {
       try {
           ZonedDateTime start = ZonedDateTime.parse(request.getAuctionStartTime());
           ZonedDateTime end = ZonedDateTime.parse(request.getAuctionEndTime());
           validateAuctionTimeRequest(request);
           Item item = findItemById(id);
           item.setAuctionStart(start.withZoneSameInstant(ZoneId.of("UTC")));
           item.setAuctionEnd(end.withZoneSameInstant(ZoneId.of("UTC")));
           item.setStatus(ItemStatus.AUCTION_SCHEDULED);
           Item saved = itemRepository.save(item);
           return itemMapper.mapToItemResponse(saved);
       }catch (ConversionException e) {
           throw new GeneralException(e.getMessage(), e.getStatus());
       } catch (DateTimeException e){
           throw new GeneralException(e.getMessage(), HttpStatus.BAD_REQUEST);
       }
    }

    private void validateAuctionTimeRequest(AuctionTimeRequest request){
        if (ZonedDateTime.parse(request.getAuctionStartTime()).isBefore(ZonedDateTime.now().plusDays(1L))){
            throw new InvalidRequestException("Auction start time must be at least 24 hours from now", HttpStatus.BAD_REQUEST);
        }
        if (ZonedDateTime.parse(request.getAuctionEndTime()).isBefore(ZonedDateTime.parse(request.getAuctionStartTime()).plusDays(1L))){
            throw new InvalidRequestException("Auction end time must be at least 24 hours from start time", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateItemRequest(ItemRequest request){
        BrandResponse brandByName = brandService.getBrandByName(request.getBrand());
        CarModelResponse model = modelService.getModelByBrandAndName(brandByName.getName(), request.getModel());
        VariantCriteria criteria = variantMapper.mapVariantToVariantCriteria(request, model.getName());
        variantService.getVariantByCriteria(criteria);
    }

    private Item updateItem(Item item, ItemRequest request){
        item.setTitle(request.getTitle());
        item.setLicensePlat(request.getLicensePlate());
        item.setBrand(request.getBrand());
        item.setModel(request.getModel());
        item.setVariant(request.getVariant());
        item.setYear(request.getYear());
        item.setFuelType(request.getFuelType());
        item.setTransmission(request.getTransmission());
        item.setMileage(request.getMileage());
        item.setStartingPrice(request.getStartingPrice());
        item.setEngineCapacity(request.getEngineCapacity());
        item.setColor(request.getColor());
        item.setInteriorGrade(request.getInteriorGrade());
        item.setExteriorGrade(request.getExteriorGrade());
        item.setChassisGrade(request.getChassisGrade());
        item.setEngineGrade(request.getEngineGrade());
        return item;
    }

    private Item findItemById(int id){
        return itemRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Item", "ID", id)
        );
    }


}
