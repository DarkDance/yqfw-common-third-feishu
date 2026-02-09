package com.lark.oapi.service.bitable.v1.enums;

/**
 * @author wiiyaya
 * @since 2026/2/7
 */
public enum SepcDateValueEnum {
    TODAY("Today"),
    TOMORROW("Tomorrow"),
    YESTERDAY("Yesterday"),
    CURRENTWEEK("CurrentWeek"),
    LASTWEEK("LastWeek"),
    CURRENTMONTH("CurrentMonth"),
    LASTMONTH("LastMonth"),
    THELASTWEEK("TheLastWeek"),
    THENEXTWEEK("TheNextWeek"),
    THELASTMONTH("TheLastMonth"),
    THENEXTMONTH("TheNextMonth");
    private final String value;

    SepcDateValueEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
