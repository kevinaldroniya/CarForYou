package com.car.foryou.helper;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuctionLockManager {
    private final ConcurrentHashMap<Integer, Object> auctionLocks = new ConcurrentHashMap<>();

    public Object getLock(Integer auctionId){
        return auctionLocks.computeIfAbsent(auctionId, k -> new Object());
    }

    public void removeLock(Integer auctionId){
        auctionLocks.remove(auctionId);
    }
}
