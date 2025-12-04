package com.coursehub.commons.feign.enums;

public enum Currency {

    USD("United States Dollar", 840),
    EUR("Euro", 978),
    GBP("British Pound", 826),
    TRY("Turkish Lira", 949),
    AZN("Azerbaijani Manat", 944);

    private final String description;
    private final int numericCode;

    Currency(String description, int numericCode) {
        this.description = description;
        this.numericCode = numericCode;
    }

    public String getDescription() {
        return description;
    }

    public int getNumericCode() {
        return numericCode;
    }
}
