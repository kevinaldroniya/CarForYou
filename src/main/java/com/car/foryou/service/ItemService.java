package com.car.foryou.service;

import com.car.foryou.dto.item.AuctionDetailRequest;
import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import org.springframework.data.domain.Page;

public interface ItemService {
    Page<ItemResponse> getAllItems(String query, int page, int size, String sortBy, String sortingDirection);
    ItemResponse getItemById(int id);
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(Long id, ItemRequest request);
    ItemResponse updateAuctionInfo(Long id, AuctionDetailRequest request);
    ItemResponse deleteItem(Long id);
}
