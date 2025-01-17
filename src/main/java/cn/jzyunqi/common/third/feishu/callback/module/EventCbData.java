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
    //加密的事件数，解密后出现下面的全部数据
    private String encrypt;

    //第一次联通数据的3个字段
    private String challenge;
    private String type;
    private String token;

    //后续的业务数据
    private String schema;
    private EventHeaderData header;
}
