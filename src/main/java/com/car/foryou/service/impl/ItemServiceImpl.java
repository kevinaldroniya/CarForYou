package com.car.foryou.service.impl;

import com.car.foryou.dto.brand.BrandResponse;
import com.car.foryou.dto.item.AuctionDetailRequest;
import com.car.foryou.dto.item.ItemFilterRequest;
import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.model.CarModelResponse;
import com.car.foryou.dto.variant.VariantCriteria;
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
import org.springframework.stereotype.Service;

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
    public Page<ItemResponse> getAllItems(String query, int page, int size, String sortBy, String sortingDirection) {
        Sort sort = sortingDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Item> itemPage = itemRepository.findAll(ItemSpecifications.hasAnyKeywords(query), pageable);
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
        validateItemRequest(request);
        Item item = itemMapper.mapItemRequestToItem(request);
        Item saved = itemRepository.save(item);
        return itemMapper.mapToItemResponse(saved);
    }

    @Override
    public ItemResponse updateItem(Long id, ItemRequest request) {
        return null;
    }

    @Override
    public ItemResponse updateAuctionInfo(Long id, AuctionDetailRequest request) {
        return null;
    }

    @Override
    public ItemResponse deleteItem(Long id) {
        return null;
    }

    private void validateItemRequest(ItemRequest request){
        BrandResponse brandByName = brandService.getBrandByName(request.getBrand());
        CarModelResponse model = modelService.getModelByBrandAndName(brandByName.getName(), request.getModel());
        VariantCriteria criteria = variantMapper.mapVariantToVariantCriteria(request, model.getName());
        variantService.getVariantByCriteria(criteria);
    }


}
