package com.lark.oapi.service.bitable.v1.enums;

/**
 * @author wiiyaya
 * @since 2025/12/11
 */
public enum MentionType {
    USER("User"),
    DOCX("Docx"),
    SHEET("Sheet"),
    BITABLE("Bitable"),
    ;

    private final String value;

    MentionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
