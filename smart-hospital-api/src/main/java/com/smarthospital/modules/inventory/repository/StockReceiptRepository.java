package com.smarthospital.modules.inventory.repository;

import com.smarthospital.modules.inventory.domain.StockReceipt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface StockReceiptRepository extends JpaRepository<StockReceipt, UUID> {

    Page<StockReceipt> findByEntryDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    Page<StockReceipt> findByItemIdAndEntryDateBetween(
            UUID itemId, LocalDate from, LocalDate to, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM stock_receipts WHERE entry_date = CURRENT_DATE",
           nativeQuery = true)
    long countToday();

    @Query(value = "SELECT COALESCE(SUM(total_cost), 0) FROM stock_receipts WHERE entry_date = CURRENT_DATE",
           nativeQuery = true)
    BigDecimal sumTodayCost();

    @Query(value = "SELECT COUNT(*) + 1 FROM stock_receipts WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
