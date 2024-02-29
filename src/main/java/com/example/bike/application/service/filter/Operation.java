package com.example.bike.application.service.filter;

public enum Operation {

    EQ,
    GT,
    GTE,
    LT,
    LTE,
    LIKE;

    private static final String EQUALS = ".eq";
    private static final String EQUALS_URL_ENCODED = "=";

    private static final String GREATER_THAN = ".gt";
    private static final String GREATER_THAN_URL_ENCODED = ">";

    private static final String GREATER_THAN_OR_EQUALS = ".gte";
    private static final String GREATER_THAN_OR_EQUALS_URL_ENCODED = ">=";

    private static final String LESS_THAN = ".lt";
    private static final String LESS_THAN_URL_ENCODED = "<";

    private static final String LESS_THAN_OR_EQUALS = ".lte";
    private static final String LESS_THAN_OR_EQUALS_URL_ENCODED = "<=";

    private static final String STR_LIKE = "*=";
    private static final String STR_LIKE_URL_ENCODED = "=~";

    public static final String[] allOperatorsNotUrlEncoded = {
            GREATER_THAN_OR_EQUALS,
            GREATER_THAN,
            LESS_THAN_OR_EQUALS,
            LESS_THAN,
            EQUALS,
            STR_LIKE
    };
    public static final String[] allOperatorsUrlEncoded = {
            GREATER_THAN_OR_EQUALS_URL_ENCODED,
            GREATER_THAN_URL_ENCODED,
            LESS_THAN_OR_EQUALS_URL_ENCODED,
            LESS_THAN_URL_ENCODED,
            EQUALS_URL_ENCODED,
            STR_LIKE_URL_ENCODED
    };


    public static Operation fromString(String op) {
        if (op != null) {
            switch (op) {
                case EQUALS:
                case EQUALS_URL_ENCODED:
                    return EQ;

                case GREATER_THAN:
                case GREATER_THAN_URL_ENCODED:
                    return GT;

                case GREATER_THAN_OR_EQUALS:
                case GREATER_THAN_OR_EQUALS_URL_ENCODED:
                    return GTE;

                case LESS_THAN:
                case LESS_THAN_URL_ENCODED:
                    return LT;

                case LESS_THAN_OR_EQUALS:
                case LESS_THAN_OR_EQUALS_URL_ENCODED:
                    return LTE;

                case STR_LIKE:
                case STR_LIKE_URL_ENCODED:
                    return LIKE;
            }
        }
        return null;
    }

}
