package com.car.foryou.service.impl;

import com.car.foryou.dto.item.AuctionDetailRequest;
import com.car.foryou.dto.item.ItemFilterRequest;
import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.dto.variant.VariantCriteria;
import com.car.foryou.exception.ResourceNotFoundException;
import com.car.foryou.mapper.ItemMapper;
import com.car.foryou.model.*;
import com.car.foryou.repository.*;
import com.car.foryou.service.ItemService;
import com.car.foryou.service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final CustomUserDetailService customUserDetailService;
    private final UserRepository userRepository;
    private final BrandRepository brandRepository;
    private final ModelRepository modelRepository;
    private final VariantRepository variantRepository;


    @Override
    public Page<ItemResponse> searchItems(String query, int page, int size, String sortBy, String sortingDirection) {
        UserDetails loggedInUserDetails = customUserDetailService.getLoggedInUserDetails();
        User user = userRepository.findByUsername(loggedInUserDetails.getUsername()).orElse(null);
        Sort sort = sortingDirection.equalsIgnoreCase(SortDirection.ASCENDING.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Item> itemPage = itemRepository.findAll(ItemSpecifications.hasAnyKeywords(query), pageable);
        return itemPage.map(itemMapper::mapToItemResponse);
    }

    @Override
    public Page<ItemResponse> getAllItems(ItemFilterRequest request) {
        return null;
    }

    @Override
    public ItemResponse createItem(ItemRequest request) {
        Brand brand = brandRepository.findByName(request.getBrand()).orElseThrow(
                () -> new ResourceNotFoundException("Brand","name",request.getBrand())
        );
        CarModel carModel = modelRepository.findByNameAndBrand(request.getModel(), brand).orElseThrow(
                () -> new ResourceNotFoundException("Model","name",request.getModel())
        );
        VariantCriteria criteria = VariantCriteria.builder()
                .name(request.getVariant())
                .model(carModel.getName())
                .year(request.getYear())
                .fuelType(request.getFuelType())
                .transmission(request.getTransmission())
                .engine(String.valueOf(request.getEngineCapacity()))
                .build();

        Variant variant = variantRepository.findOne(VariantSpecifications.hasCriteria(criteria)).orElseThrow(
                () -> new ResourceNotFoundException("Variant", "criteria", criteria.toString())
        );

        Integer id = CustomUserDetailService.getLoggedInUserDetails().getId();
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "ID", id)
        );
        Item item = itemMapper.mapItemRequestToItem(request, user, variant);
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
}
