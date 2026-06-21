package com.smartclinic.modules.bloodbank.repository;

import com.smartclinic.modules.bloodbank.domain.BloodIssue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BloodIssueRepository extends JpaRepository<BloodIssue, UUID> {

    List<BloodIssue> findByRequestId(UUID requestId);

    Page<BloodIssue> findAllByOrderByIssueDateDesc(Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM blood_issues WHERE DATE(issue_date) = CURRENT_DATE",
           nativeQuery = true)
    long countToday();

    @Query(value = "SELECT COUNT(*) + 1 FROM blood_issues WHERE EXTRACT(YEAR FROM created_at) = :year",
           nativeQuery = true)
    long nextSequenceForYear(@Param("year") int year);
}
