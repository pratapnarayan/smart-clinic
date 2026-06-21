package com.smartclinic.modules.ipd.repository;

import com.smartclinic.modules.ipd.domain.IpdWard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IpdWardRepository extends JpaRepository<IpdWard, UUID> {
    List<IpdWard> findByActiveTrue();
    boolean existsByNameIgnoreCase(String name);
}
