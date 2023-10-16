package com.example.demo.item.repository;

import com.example.demo.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionRepository {
    Page<Item> findPurchaseItemByMember_id(Long id, Pageable pageable);
    Page<Item> findSellingItemByMember_id(Long id, Pageable pageable);
}
