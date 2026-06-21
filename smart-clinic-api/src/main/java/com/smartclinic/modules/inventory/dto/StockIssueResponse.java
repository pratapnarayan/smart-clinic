package com.smartclinic.modules.inventory.dto;

import com.smartclinic.modules.inventory.domain.StockIssue;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record StockIssueResponse(
        UUID      id,
        String    issueNumber,
        LocalDate issueDate,
        UUID      itemId,
        String    itemName,
        String    itemUnit,
        int       quantity,
        String    issuedTo,
        String    issuedBy,
        String    purpose,
        String    notes,
        Instant   createdAt
) {
    public static StockIssueResponse from(StockIssue i) {
        return new StockIssueResponse(
                i.getId(), i.getIssueNumber(), i.getIssueDate(),
                i.getItemId(), i.getItemName(), i.getItemUnit(),
                i.getQuantity(), i.getIssuedTo(), i.getIssuedBy(),
                i.getPurpose(), i.getNotes(), i.getCreatedAt()
        );
    }
}
