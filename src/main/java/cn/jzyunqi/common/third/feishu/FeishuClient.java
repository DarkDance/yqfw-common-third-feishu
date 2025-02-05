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
            Client client = Client.newBuilder(feishuAuth.getAppId(), feishuAuth.getAppSecret())
                    .requestTimeout(5, TimeUnit.SECONDS)
                    //.disableTokenCache()
                    .logReqAtDebug(true)
                    .build();
            clientMap.put(feishuAuth.getAppId(), client);
        }
    }

    public Client chooseClient(String feishuAuthId) {
        return clientMap.get(feishuAuthId);
    }
}
