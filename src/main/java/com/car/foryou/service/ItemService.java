package com.car.foryou.service;

import com.car.foryou.dto.item.AuctionDetailRequest;
import com.car.foryou.dto.item.ItemFilterRequest;
import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    Page<ItemResponse> searchItems(String query, int page, int size, String sortBy, String sortingDirection);
    Page<ItemResponse> getAllItems(ItemFilterRequest request);
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(Long id, ItemRequest request);
    ItemResponse updateAuctionInfo(Long id, AuctionDetailRequest request);
    ItemResponse deleteItem(Long id);
}
