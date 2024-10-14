package com.car.foryou.service.item;

import com.car.foryou.dto.item.*;
import org.springframework.data.domain.Page;

public interface ItemService {
    Page<ItemResponse> getAllItems(ItemFilterRequest filterRequest);
    ItemResponse getItemById(int id);
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(Integer id, ItemRequest request);
    ItemResponse deleteItem(Long id);
    ItemResponse updateItemAuctionTime(Integer id, ItemAuctionTimeRequest request);
}
