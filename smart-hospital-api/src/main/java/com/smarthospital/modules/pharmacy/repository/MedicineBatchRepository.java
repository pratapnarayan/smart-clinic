package com.smarthospital.modules.pharmacy.repository;

import com.smarthospital.modules.pharmacy.domain.MedicineBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MedicineBatchRepository extends JpaRepository<MedicineBatch, UUID> {

    List<MedicineBatch> findByMedicineIdOrderByExpiryDateAsc(UUID medicineId);

    /** Non-expired batches with stock, FEFO order (First Expiry First Out) */
    @Query("SELECT b FROM MedicineBatch b WHERE b.medicine.id = :medicineId " +
           "AND b.quantity > 0 AND b.expiryDate > :today ORDER BY b.expiryDate ASC")
    List<MedicineBatch> findAvailableBatches(@Param("medicineId") UUID medicineId,
                                             @Param("today") LocalDate today);

    /** Batches expiring within the given number of days */
    @Query("SELECT b FROM MedicineBatch b WHERE b.expiryDate <= :cutoff AND b.quantity > 0")
    List<MedicineBatch> findExpiringBefore(@Param("cutoff") LocalDate cutoff);

    boolean existsByMedicineIdAndBatchNumber(UUID medicineId, String batchNumber);

    /** Total available stock across all non-expired batches for a medicine */
    @Query("SELECT COALESCE(SUM(b.quantity), 0) FROM MedicineBatch b " +
           "WHERE b.medicine.id = :medicineId AND b.expiryDate > :today")
    int totalAvailableStock(@Param("medicineId") UUID medicineId,
                            @Param("today") LocalDate today);
}
