package cn.jzyunqi.common.third.feishu;

import cn.jzyunqi.common.utils.StringUtilPlus;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wiiyaya
 * @since 2025/1/12
 */
@Getter
@Setter
public class FeishuAuth {

    private String appId;
    private String appSecret;
    private String verificationToken;
    private String encryptKey;
    private Boolean wsConnect;

    public FeishuAuth() {
    }

    public FeishuAuth(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.verificationToken = StringUtilPlus.EMPTY;
        this.encryptKey = StringUtilPlus.EMPTY;
        this.wsConnect = Boolean.TRUE;
    }

    public FeishuAuth(String appId, String appSecret, String verificationToken, String encryptKey, Boolean wsConnect) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.verificationToken = StringUtilPlus.defaultString(verificationToken);
        this.encryptKey = StringUtilPlus.defaultString(encryptKey);
        this.wsConnect = wsConnect;
    }
}
