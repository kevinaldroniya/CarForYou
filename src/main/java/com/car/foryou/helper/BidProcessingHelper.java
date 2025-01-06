package com.car.foryou.helper;

import com.car.foryou.dto.bid.BidRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
public class BidProcessingHelper {
    private final BlockingQueue<BidRequest> bidQueue = new LinkedBlockingQueue<>();

    @PostConstruct
    public void init() {
        startBidProcessing();
    }

    public void submitBid(BidRequest bidRequest) {
        try {
            bidQueue.put(bidRequest); // Enqueue bid requests
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to enqueue bid request", e);
        }
    }

    private void startBidProcessing() {
        Thread worker = new Thread(() -> {
            while (true) {
                try {
                    BidRequest bidRequest = bidQueue.take(); // Dequeue and process bid
                    processBid(bidRequest);
                } catch (Exception e) {
                    log.error("Error processing bid: {}", e.getMessage(), e);
                }
            }
        });
        worker.setDaemon(true);
        worker.start();
    }

    private void processBid(BidRequest bidRequest) {
        // Call the actual bid placement logic here
        bidRequest.getBidHandler().run();
    }
}
