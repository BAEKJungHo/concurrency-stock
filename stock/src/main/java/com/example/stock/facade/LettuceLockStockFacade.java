package com.example.stock.facade;

import com.example.stock.repository.RedisRepository;
import com.example.stock.service.LettuceLockStockService;
import com.example.stock.service.RedisLockStockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private final RedisRepository redisRepository;

    private final LettuceLockStockService stockService;

    public LettuceLockStockFacade(RedisRepository redisRepository, LettuceLockStockService stockService) {
        this.redisRepository = redisRepository;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) throws InterruptedException {
        while (!redisRepository.lock(key)) {
            // fail to acquire lock then thread sleep
            Thread.sleep(100);
        }

        // success to acquire lock
        try {
            stockService.decrease(key, quantity);
        } finally {
            redisRepository.unlock(key);
        }
    }
}
