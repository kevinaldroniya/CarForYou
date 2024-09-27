package com.car.foryou.service.impl;

import com.car.foryou.dto.item.AuctionDetailRequest;
import com.car.foryou.dto.item.ItemFilterRequest;
import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.mapper.ItemMapper;
import com.car.foryou.model.Item;
import com.car.foryou.model.User;
import com.car.foryou.repository.ItemRepository;
import com.car.foryou.repository.ItemSpecifications;
import com.car.foryou.repository.UserRepository;
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
        return null;
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
