package com.lark.oapi.service.im.v1.model.ext;

import com.google.gson.annotations.SerializedName;

/**
 * @author wiiyaya
 * @since 2025/7/31
 */
public class MessagePostMd extends MessagePostElement {

    @SerializedName("text")
    private String text;

    public MessagePostMd() {
        this.tag = "md";
    }

    public MessagePostMd(Builder builder) {
        this.tag = "md";
        this.text = builder.text;
    }

    @Override
    String getTag() {
        return this.tag;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String text;

        public Builder() {
        }

        public Builder text(String text) {
            this.text = text;
            return this;
        }

        public MessagePostMd build() {
            return new MessagePostMd(this);
        }
    }
}
