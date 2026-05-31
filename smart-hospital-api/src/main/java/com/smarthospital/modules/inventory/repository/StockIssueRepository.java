package com.smarthospital.modules.inventory.repository;

import com.smarthospital.modules.inventory.domain.StockIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface StockIssueRepository extends JpaRepository<StockIssue, UUID> {

    Page<StockIssue> findByIssueDateBetween(LocalDate from, LocalDate to, Pageable pageable);

    Page<StockIssue> findByItemIdAndIssueDateBetween(
            UUID itemId, LocalDate from, LocalDate to, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM stock_issues WHERE issue_date = CURRENT_DATE",
           nativeQuery = true)
    long countToday();

    @Query(value = "SELECT COUNT(*) + 1 FROM stock_issues WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
