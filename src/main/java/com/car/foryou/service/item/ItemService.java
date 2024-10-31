package com.car.foryou.service.item;

import com.car.foryou.dto.item.*;
import com.car.foryou.model.Item;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemService {
    List<Item> getAllItems();
    Item getItemById(Integer id);
    Page<ItemResponse> getAllItemsResponse(ItemFilterRequest filterRequest);
    ItemResponse getItemResponseById(Integer id);
    ItemResponse createItem(ItemRequest request);
    ItemResponse updateItem(Integer id, ItemRequest request);
    ItemResponse deleteItem(Long id);
    ItemResponse updateItemAuctionTime(Integer id, ItemAuctionTimeRequest request);
    ItemResponse updateItemStatus(Integer id, ItemStatus status);
}
