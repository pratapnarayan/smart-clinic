package com.smarthospital.modules.frontoffice.repository;

import com.smarthospital.modules.frontoffice.domain.OpdToken;
import com.smarthospital.modules.frontoffice.domain.OpdToken.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OpdTokenRepository extends JpaRepository<OpdToken, UUID> {

    List<OpdToken> findByTokenDateAndDepartmentOrderByTokenNumberAsc(LocalDate date, String department);

    List<OpdToken> findByTokenDateAndDepartmentAndStatusOrderByTokenNumberAsc(
            LocalDate date, String department, TokenStatus status);

    List<OpdToken> findByTokenDateOrderByDepartmentAscTokenNumberAsc(LocalDate date);

    long countByTokenDateAndDepartmentAndStatus(LocalDate date, String department, TokenStatus status);

    /** Next token sequence for a given date+department */
    @Query(value = "SELECT COUNT(*) + 1 FROM opd_tokens WHERE token_date = :date AND department = :dept",
           nativeQuery = true)
    long nextTokenNumber(@Param("date") LocalDate date, @Param("dept") String department);
}
