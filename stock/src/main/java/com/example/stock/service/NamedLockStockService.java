package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class NamedLockStockService {

    private final StockRepository stockRepository;

    public NamedLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * 부모의 트랜잭션과 동일한 범위로 묶인다면 Database 에 commit 되기 전에 Lock 이 풀리는 현상이 발생함
     * 그렇기 때문에 별도의 트랜잭션으로 분리하여 Database 에 commit 이 된 후, Lock 을 해제하도록 함
     * 즉, Propagation.REQUIRES_NEW 설정을 통해, Lock 을 해제하기 전에 Database 에 commit 이 되도록 함
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        stockRepository.saveAndFlush(stock);
    }
}
