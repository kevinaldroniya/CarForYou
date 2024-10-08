package com.car.foryou.service;

import com.car.foryou.dto.item.*;
import org.springframework.data.domain.Page;

public interface ItemService {
    Page<ItemResponse> getAllItems(ItemFilterRequest filterRequest);
    ItemResponse getItemById(int id);
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(Integer id, ItemRequest request);
    ItemResponse updateAuctionInfo(Long id, AuctionDetailRequest request);
    ItemResponse deleteItem(Long id);
    ItemResponse updateItemAuctionTime(Integer id, AuctionTimeRequest request);
}
