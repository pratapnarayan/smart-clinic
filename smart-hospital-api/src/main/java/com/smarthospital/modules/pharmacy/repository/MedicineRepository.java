package com.smarthospital.modules.pharmacy.repository;

import com.smarthospital.modules.pharmacy.domain.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MedicineRepository extends JpaRepository<Medicine, UUID> {

    Page<Medicine> findByCategoryId(UUID categoryId, Pageable pageable);

    @Query("SELECT m FROM Medicine m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%',:q,'%'))")
    Page<Medicine> searchByName(@Param("q") String query, Pageable pageable);

    boolean existsByNameIgnoreCaseAndCategoryId(String name, UUID categoryId);

    /** Medicines whose total batch stock is at or below reorder level — for low-stock report. */
    @Query(value = """
            SELECT m.* FROM medicines m
            WHERE m.deleted_at IS NULL
              AND (SELECT COALESCE(SUM(b.quantity),0)
                   FROM medicine_batches b WHERE b.medicine_id = m.id
                     AND b.expiry_date > CURRENT_DATE) <= m.reorder_level
            """, nativeQuery = true)
    List<Medicine> findLowStockMedicines();
}
