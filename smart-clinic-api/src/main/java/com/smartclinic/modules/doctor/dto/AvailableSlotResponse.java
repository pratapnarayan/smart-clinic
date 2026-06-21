package com.smartclinic.modules.doctor.dto;

import java.time.LocalDate;
import java.util.List;

public record AvailableSlotResponse(LocalDate date, List<String> slots) {}
