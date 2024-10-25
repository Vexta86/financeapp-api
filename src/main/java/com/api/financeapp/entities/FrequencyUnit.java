package com.api.financeapp.entities;

import com.fasterxml.jackson.databind.annotation.EnumNaming;


public enum FrequencyUnit {
    DAYS("Days"),
    WEEKS("Weeks"),
    MONTHS("Months"),
    YEARS("Years");
    private final String displayName;

    FrequencyUnit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
