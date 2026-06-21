package com.smartclinic.modules.bloodbank.repository;

import com.smartclinic.modules.bloodbank.domain.BloodDonor;
import com.smartclinic.modules.bloodbank.domain.BloodGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BloodDonorRepository extends JpaRepository<BloodDonor, UUID> {

    Page<BloodDonor> findByBloodGroupAndActiveTrue(BloodGroup bloodGroup, Pageable pageable);

    @Query("SELECT d FROM BloodDonor d WHERE " +
           "LOWER(CONCAT(d.firstName,' ',d.lastName)) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "d.mobile LIKE CONCAT('%',:q,'%')")
    Page<BloodDonor> search(@Param("q") String q, Pageable pageable);

    long countByActiveTrue();

    @Query(value = "SELECT COUNT(*) + 1 FROM blood_donors WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
