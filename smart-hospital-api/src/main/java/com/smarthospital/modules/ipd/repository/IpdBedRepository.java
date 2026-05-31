package com.smarthospital.modules.ipd.repository;

import com.smarthospital.modules.ipd.domain.IpdBed;
import com.smarthospital.modules.ipd.domain.IpdBed.BedStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface IpdBedRepository extends JpaRepository<IpdBed, UUID> {
    List<IpdBed> findByWardId(UUID wardId);
    List<IpdBed> findByWardIdAndStatus(UUID wardId, BedStatus status);
    boolean existsByWardIdAndBedNumberIgnoreCase(UUID wardId, String bedNumber);

    @Query("SELECT COUNT(b) FROM IpdBed b WHERE b.wardId = :wardId AND b.status = 'AVAILABLE'")
    long countAvailableByWard(@Param("wardId") UUID wardId);
}
