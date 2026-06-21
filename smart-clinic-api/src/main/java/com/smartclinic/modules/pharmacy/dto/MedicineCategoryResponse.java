package com.smartclinic.modules.pharmacy.dto;

import com.smartclinic.modules.pharmacy.domain.MedicineCategory;

import java.util.UUID;

public record MedicineCategoryResponse(UUID id, String name) {
    public static MedicineCategoryResponse from(MedicineCategory c) {
        return new MedicineCategoryResponse(c.getId(), c.getName());
    }
}
