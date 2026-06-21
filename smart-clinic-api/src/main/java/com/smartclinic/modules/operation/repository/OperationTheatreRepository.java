package com.smartclinic.modules.operation.repository;

import com.smartclinic.modules.operation.domain.OperationTheatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OperationTheatreRepository extends JpaRepository<OperationTheatre, UUID> {
    List<OperationTheatre> findByActiveTrue();
    boolean existsByTheatreNumberIgnoreCase(String theatreNumber);
}
