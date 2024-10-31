package com.car.foryou.service.item;

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
import com.car.foryou.repository.item.ItemRepository;
import com.car.foryou.repository.item.ItemSpecifications;
import com.car.foryou.service.brand.BrandService;
import com.car.foryou.service.model.CarModelService;
import com.car.foryou.service.user.CustomUserDetailService;
import com.car.foryou.service.variant.VariantService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

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
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItemById(Integer id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("ITEM", "ID", id)
        );
    }

    @Override
    public Page<ItemResponse> getAllItemsResponse(ItemFilterRequest filterRequest) {
        Sort sort = filterRequest.getSortDirection().equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(filterRequest.getSortBy()).ascending() : Sort.by(filterRequest.getSortBy()).descending();
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize(), sort);
        Page<Item> itemPage = itemRepository.findAll(ItemSpecifications.hasAnyKeywords(filterRequest), pageable);
        return itemPage.map(itemMapper::mapToItemResponse);
    }

    @Override
    public ItemResponse getItemResponseById(Integer id) {
        return itemMapper.mapToItemResponse(getItemById(id));
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
            Item item = getItemById(id);
            validateItemRequest(request);
            if (!item.getStatus().equals(ItemStatus.AVAILABLE)){
                throw new InvalidRequestException("Item is not available for update", HttpStatus.BAD_REQUEST);
            }
            Item updated = updateItem(item, request);
            Item saved = itemRepository.save(updated);
            return itemMapper.mapToItemResponse(saved);
        }catch (ResourceNotFoundException e){
            throw new GeneralException(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ItemResponse deleteItem(Long id) {
        return null;
    }

    @Override
    public ItemResponse updateItemAuctionTime(Integer id, ItemAuctionTimeRequest request) {
       try {
           User auctioneer = User.builder()
                   .id(CustomUserDetailService.getLoggedInUserDetails().getId())
                   .build();
           Item item = getItemById(id);
           if (!item.getStatus().equals(ItemStatus.AVAILABLE)){
               throw new InvalidRequestException("Item is not available for auction", HttpStatus.BAD_REQUEST);
           }
           ZonedDateTime start = ZonedDateTime.parse(request.getAuctionStartTime());
           ZonedDateTime end = ZonedDateTime.parse(request.getAuctionEndTime());
           validateAuctionTimeRequest(request);
           item.setAuctionStart(start.withZoneSameInstant(ZoneId.of("UTC")));
           item.setAuctionEnd(end.withZoneSameInstant(ZoneId.of("UTC")));
           item.setStatus(ItemStatus.AUCTION_SCHEDULED);
           item.setAuctioneer(auctioneer);
           Item saved = itemRepository.save(item);
           return itemMapper.mapToItemResponse(saved);
       }catch (ConversionException e) {
           throw new GeneralException(e.getMessage(), e.getStatus());
       } catch (DateTimeException e){
           throw new GeneralException(e.getMessage(), HttpStatus.BAD_REQUEST);
       }
    }

    @Transactional
    @Override
    public ItemResponse updateItemStatus(Integer id, ItemStatus status){
        if (status.equals(ItemStatus.AUCTION_SCHEDULED)){
            throw new InvalidRequestException("Invalid status", HttpStatus.BAD_REQUEST);
        }
        Item item = getItemById(id);
        item.setStatus(status);
        Item saved = itemRepository.save(item);
        return itemMapper.mapToItemResponse(saved);
    }

    private void validateAuctionTimeRequest(ItemAuctionTimeRequest request){
        if (ZonedDateTime.parse(request.getAuctionStartTime()).isBefore(ZonedDateTime.now().plusDays(1L))){
            throw new InvalidRequestException("Auction start time must be at least 24 hours from now", HttpStatus.BAD_REQUEST);
        }
        if (ZonedDateTime.parse(request.getAuctionEndTime()).isBefore(ZonedDateTime.parse(request.getAuctionStartTime()).plusDays(1L))){
            throw new InvalidRequestException("Auction end time must be at least 24 hours from start time", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateItemRequest(ItemRequest request){
        BrandResponse brandByName = brandService.getBrandResponseByName(request.getBrand());
        CarModelResponse model = modelService.getModelResponseByBrandAndName(brandByName.getName(), request.getModel());
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
        item.setInteriorItemGrade(request.getInteriorItemGrade());
        item.setExteriorItemGrade(request.getExteriorItemGrade());
        item.setChassisItemGrade(request.getChassisItemGrade());
        item.setEngineItemGrade(request.getEngineItemGrade());
        return item;
    }
}
