package cn.jzyunqi.common.third.feishu.callback.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author wiiyaya
 * @since 2025/1/8
 */
@Getter
@Setter
@ToString
public class EventHeaderData {
    /**
     * 应 Verification Token
     */
    private String token;

    /**
     * 回调发送的时间戳，近似于回调触发的时间
     */
    @JsonProperty("create_time")
    private Long createTime;

    /**
     * 回调类型
     */
    @JsonProperty("event_type")
    private String eventType;

    /**
     * 回调所属应用的租户id
     */
    @JsonProperty("tenant_key")
    private String tenantKey;

    /**
     * 回调所属应用的应用id
     */
    @JsonProperty("app_id")
    private String appId;
}
