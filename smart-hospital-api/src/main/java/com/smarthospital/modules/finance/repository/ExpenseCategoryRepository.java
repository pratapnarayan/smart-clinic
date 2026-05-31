package com.smarthospital.modules.finance.repository;

import com.smarthospital.modules.finance.domain.ExpenseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory, UUID> {
    List<ExpenseCategory> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
