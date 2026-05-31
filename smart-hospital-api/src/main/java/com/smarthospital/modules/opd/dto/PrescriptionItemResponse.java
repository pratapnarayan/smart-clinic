package com.smarthospital.modules.opd.dto;

import com.smarthospital.modules.opd.domain.PrescriptionItem;

import java.util.UUID;

public record PrescriptionItemResponse(
        UUID   id,
        String medicineName,
        String dose,
        String frequency,
        String duration,
        String instructions
) {
    public static PrescriptionItemResponse from(PrescriptionItem i) {
        return new PrescriptionItemResponse(
                i.getId(), i.getMedicineName(), i.getDose(),
                i.getFrequency(), i.getDuration(), i.getInstructions());
    }
}
