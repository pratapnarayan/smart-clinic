package com.smarthospital.modules.pathology.dto;

import com.smarthospital.modules.pathology.domain.LabOrderItem;
import com.smarthospital.modules.pathology.domain.LabOrderItem.ItemStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LabOrderItemResponse(
        UUID          id,
        UUID          testId,
        String        testCode,
        String        testName,
        BigDecimal    price,
        String        unit,
        String        normalRange,
        ItemStatus    status,
        String        result,
        String        resultNote,
        LocalDateTime resultEnteredAt,
        String        resultEnteredBy
) {
    public static LabOrderItemResponse from(LabOrderItem i) {
        return new LabOrderItemResponse(
                i.getId(), i.getTestId(), i.getTestCode(), i.getTestName(),
                i.getPrice(), i.getUnit(), i.getNormalRange(),
                i.getStatus(), i.getResult(), i.getResultNote(),
                i.getResultEnteredAt(), i.getResultEnteredBy()
        );
    }
}
