package com.lark.oapi.service.im.v1.model.ext;

import com.google.gson.annotations.SerializedName;
import com.lark.oapi.core.utils.Jsons;

/**
 * @author wiiyaya
 * @since 2025/10/8
 */
public class MessageFilePatch {

    @SerializedName("file_key")
    private String fileKey;

    @SerializedName("file_name")
    private String fileName;

    public MessageFilePatch() {
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        @SerializedName("file_key")
        private String fileKey;

        @SerializedName("file_name")
        private String fileName;

        public Builder() {
        }

        public Builder fileKey(String fileKey) {
            this.fileKey = fileKey;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public String build() {
            return Jsons.DEFAULT.toJson(this);
        }
    }
}
