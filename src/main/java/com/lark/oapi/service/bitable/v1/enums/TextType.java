package com.lark.oapi.service.bitable.v1.enums;

/**
 * @author wiiyaya
 * @since 2025/12/11
 */
public enum TextType {
    /**
     * 纯文本类型
     */
    TEXT("text"),

    /**
     * 提及人或提及云文档类型
     */
    MENTION("mention"),

    /**
     * 超链接类型
     */
    URL("url");

    private final String value;

    TextType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
