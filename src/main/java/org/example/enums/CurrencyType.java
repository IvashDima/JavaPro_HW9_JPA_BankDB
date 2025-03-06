package org.example.enums;

public enum CurrencyType {
    USD, EUR, UAH;

    public static CurrencyType fromString(String input) {
        try {
            return CurrencyType.valueOf(input.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
