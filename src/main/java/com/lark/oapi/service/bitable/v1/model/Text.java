package com.lark.oapi.service.bitable.v1.model;

import com.google.gson.annotations.SerializedName;

/**
 * @author wiiyaya
 * @since 2025/12/11
 */
public class Text {

    /**
     * 文本展示类型
     * com.lark.oapi.service.bitable.v1.enums.TextType解析
     * <p> 示例值：mention
     */
    @SerializedName("type")
    private String type;

    /**
     * 当 type 字段为 mention 时，该字段有效
     * com.lark.oapi.service.bitable.v1.enums.MentionType
     * <p> 示例值：User
     */
    @SerializedName("mentionType")
    private String mentionType;

    /**
     * 文本内容
     * <p> 示例值：黄泡泡
     */
    @SerializedName("text")
    private String text;

    /**
     * 当 type 字段为 mention 时，该字段有效。
     * 当 mentionType 为 User 时，token 为用户 ID
     * 当 mentionType 为 Docx 时，token 为文档的 document_id
     * 当 mentionType 为 Sheet 时，token 为电子表格的 spreadsheet_token
     * 当 mentionType 为 Bitable时，token 为多维表格的 app_token
     */
    @SerializedName("token")
    private String token;

    /**
     * 链接。当 type 字段为 url 时，该字段有效
     * <p> 示例值：https://example.com/avatar
     */
    @SerializedName("link")
    private String link;

    /**
     * 当 type 字段为 mention 且 mentionType 字段为 User 时，该字段有效。可选值有：
     * false：不提及该用户
     * true：提及该用户
     */
    @SerializedName("mentionNotify")
    private Boolean mentionNotify;

    /**
     * 提及的用户名称。当 type 字段为 mention 且 mentionType 字段为 User 时，该字段有效
     */
    @SerializedName("name")
    private String name;

    // builder 开始
    public Text() {
    }

    public Text(Text.Builder builder) {
        this.type = builder.type;
        this.mentionType = builder.mentionType;
        this.text = builder.text;
        this.token = builder.token;
        this.link = builder.link;
        this.mentionNotify = builder.mentionNotify;
        this.name = builder.name;
    }

    public static Text.Builder newBuilder() {
        return new Text.Builder();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMentionType() {
        return mentionType;
    }

    public void setMentionType(String mentionType) {
        this.mentionType = mentionType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getMentionNotify() {
        return mentionNotify;
    }

    public void setMentionNotify(Boolean mentionNotify) {
        this.mentionNotify = mentionNotify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static class Builder {
        /**
         * 文本展示类型
         * com.lark.oapi.service.bitable.v1.enums.TextType解析
         * <p> 示例值：mention
         */
        private String type;

        /**
         * 当 type 字段为 mention 时，该字段有效
         * com.lark.oapi.service.bitable.v1.enums.MentionType
         * <p> 示例值：User
         */
        private String mentionType;

        /**
         * 文本内容
         * <p> 示例值：黄泡泡
         */
        private String text;

        /**
         * 当 type 字段为 mention 时，该字段有效。
         * 当 mentionType 为 User 时，token 为用户 ID
         * 当 mentionType 为 Docx 时，token 为文档的 document_id
         * 当 mentionType 为 Sheet 时，token 为电子表格的 spreadsheet_token
         * 当 mentionType 为 Bitable时，token 为多维表格的 app_token
         */
        private String token;

        /**
         * 链接。当 type 字段为 url 时，该字段有效
         * <p> 示例值：https://example.com/avatar
         */
        private String link;

        /**
         * 当 type 字段为 mention 且 mentionType 字段为 User 时，该字段有效。可选值有：
         * false：不提及该用户
         * true：提及该用户
         */
        private Boolean mentionNotify;

        /**
         * 提及的用户名称。当 type 字段为 mention 且 mentionType 字段为 User 时，该字段有效
         */
        private String name;

        public Text.Builder name(String name) {
            this.name = name;
            return this;
        }

        public Text.Builder mentionNotify(Boolean mentionNotify) {
            this.mentionNotify = mentionNotify;
            return this;
        }

        public Text.Builder link(String link) {
            this.link = link;
            return this;
        }

        public Text.Builder token(String token) {
            this.token = token;
            return this;
        }

        public Text.Builder text(String text) {
            this.text = text;
            return this;
        }

        public Text.Builder mentionType(String mentionType) {
            this.mentionType = mentionType;
            return this;
        }

        public Text.Builder type(String type) {
            this.type = type;
            return this;
        }

        public Text build() {
            return new Text(this);
        }
    }
}
