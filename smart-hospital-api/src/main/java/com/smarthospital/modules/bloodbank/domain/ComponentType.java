package com.smarthospital.modules.bloodbank.domain;

public enum ComponentType {
    WHOLE_BLOOD, PACKED_CELLS, FRESH_FROZEN_PLASMA, PLATELET_CONCENTRATE;

    /** Default shelf-life in days for automatic expiry calculation. */
    public int defaultShelfDays() {
        return switch (this) {
            case WHOLE_BLOOD, PACKED_CELLS       -> 42;
            case FRESH_FROZEN_PLASMA             -> 365;
            case PLATELET_CONCENTRATE            -> 5;
        };
    }
}
