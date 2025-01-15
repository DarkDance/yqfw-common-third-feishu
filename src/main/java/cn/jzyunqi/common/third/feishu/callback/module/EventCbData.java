package cn.jzyunqi.common.third.feishu.callback.module;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author wiiyaya
 * @since 2025/1/8
 */
@Getter
@Setter
@ToString
public class EventCbData {
    /**
     * 加密的事件数，解密后出现下面的全部数据
     */
    private String encrypt;

    /**
     * 第一次联通数据，应用需要在响应中原样返回的值
     */
    private String challenge;

    /**
     * 第一次联通数据，固定值为url_verification
     */
    private String type;

    /**
     * 第一次联通数据，校验用
     */
    private String token;

    private String schema;
    private EventHeaderData header;
    private Map<String, Object> event;
}
