package com.car.foryou.controller;

import com.car.foryou.dto.item.*;
import com.car.foryou.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService){
        this.itemService = itemService;
    }


    @GetMapping()
    public ResponseEntity<Page<ItemResponse>> getAllItems(@ModelAttribute ItemFilterRequest filterRequest){
        Page<ItemResponse> itemResponses = itemService.getAllItems(filterRequest);
        return new ResponseEntity<>(itemResponses, HttpStatus.OK);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ItemResponse>> filter(@ModelAttribute ItemFilterRequest itemFilterRequest){
        return null;
    }

    @PostMapping
    public ResponseEntity<ItemResponse> createItem(@RequestBody ItemRequest request){
        ItemResponse response = itemService.createItem(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping(
            path = "/{id}"
    )
    public ResponseEntity<ItemResponse> updateItem(@PathVariable("id") Long id, @RequestBody ItemRequest request){
        return null;
    }

    @GetMapping(
            path = "/{id}"
    )
    public ResponseEntity<ItemResponse> getItemById(@PathVariable("id") Integer id){
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @DeleteMapping(
            path = "/{id}"
    )
    public ResponseEntity<ItemResponse> deleteItem(@PathVariable("id") Long id){
        return null;
    }

    @PreAuthorize("hasRole('AUCTIONEER')")
    @PutMapping("setAuction/{id}")
    public ResponseEntity<ItemResponse> updateAuctionTimeInfo(@PathVariable("id") Integer id, @Valid @RequestBody AuctionTimeRequest request){
        return ResponseEntity.ok(itemService.updateItemAuctionTime(id, request));
    }
}
