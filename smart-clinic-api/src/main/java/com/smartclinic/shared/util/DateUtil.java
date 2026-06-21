package com.smartclinic.shared.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class DateUtil {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    private DateUtil() {}

    public static String format(LocalDate date) {
        return (date != null) ? date.format(DISPLAY_FORMAT) : null;
    }

    public static LocalDate toLocalDate(Instant instant) {
        return (instant != null) ? instant.atZone(ZoneId.systemDefault()).toLocalDate() : null;
    }
}
