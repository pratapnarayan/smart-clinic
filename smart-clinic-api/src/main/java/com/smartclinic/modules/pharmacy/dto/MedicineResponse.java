package com.smartclinic.modules.pharmacy.dto;

import com.smartclinic.modules.pharmacy.domain.Medicine;

import java.util.UUID;

public record MedicineResponse(
        UUID   id,
        UUID   categoryId,
        String categoryName,
        String name,
        String genericName,
        String unit,
        int    reorderLevel,
        int    availableStock    // populated by service from batch sum
) {
    public static MedicineResponse from(Medicine m, int availableStock) {
        return new MedicineResponse(
                m.getId(),
                m.getCategory().getId(),
                m.getCategory().getName(),
                m.getName(),
                m.getGenericName(),
                m.getUnit(),
                m.getReorderLevel(),
                availableStock
        );
    }
}
