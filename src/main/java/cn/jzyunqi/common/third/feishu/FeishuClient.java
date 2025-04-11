package cn.jzyunqi.common.third.feishu;

import com.lark.oapi.Client;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
public class FeishuClient {

    @Resource
    private FeishuAuthRepository feishuClientConfig;

    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<FeishuAuth> feishuAuthList = feishuClientConfig.getFeishuAuthList();
        for (FeishuAuth feishuAuth : feishuAuthList) {
            clientMap.put(feishuAuth.getAppId(), getClient(feishuAuth));
        }
    }

    /**
     * 选择一个飞书应用
     *
     * @param feishuAuthId 飞书应用ID
     * @return 飞书应用
     */
    public Client chooseClient(String feishuAuthId) {
        return clientMap.get(feishuAuthId);
    }

    /**
     * 添加一个新的飞书应用
     *
     * @param feishuAuth 飞书应用授权信息
     */
    public void addClient(FeishuAuth feishuAuth) {
        if (!clientMap.containsKey(feishuAuth.getAppId())) {
            clientMap.put(feishuAuth.getAppId(), getClient(feishuAuth));
        }
    }

    /**
     * 组装飞书应用客户端
     *
     * @param feishuAuth 飞书应用授权信息
     * @return 飞书应用客户端
     */
    private Client getClient(FeishuAuth feishuAuth) {
        return Client.newBuilder(feishuAuth.getAppId(), feishuAuth.getAppSecret())
                .requestTimeout(5, TimeUnit.SECONDS)
                //.disableTokenCache()
                .logReqAtDebug(true)
                .build();
    }
}
