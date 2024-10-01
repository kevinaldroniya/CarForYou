package com.car.foryou.controller;

import com.car.foryou.dto.item.AuctionDetailRequest;
import com.car.foryou.dto.item.ItemFilterRequest;
import com.car.foryou.dto.item.ItemRequest;
import com.car.foryou.dto.item.ItemResponse;
import com.car.foryou.service.ItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService){
        this.itemService = itemService;
    }


    @GetMapping("/search")
    public ResponseEntity<Page<ItemResponse>> search(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortingDirection", defaultValue = "ASC") String sortingDirection,
            @RequestParam(name = "SortBy", defaultValue = "id") String sortBy
    ){

        Page<ItemResponse> itemResponses = itemService.searchItems(query, page, size, sortBy, sortingDirection);
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

    @PatchMapping(
            path = "/{id}"
    )
    public ResponseEntity<ItemResponse> updateAuctionInfo(@PathVariable("id") Long id, @RequestBody AuctionDetailRequest request){
        return null;
    }

    @DeleteMapping(
            path = "/{id}"
    )
    public ResponseEntity<ItemResponse> deleteItem(@PathVariable("id") Long id){
        return null;
    }
}
