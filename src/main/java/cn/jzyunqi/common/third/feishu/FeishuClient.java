package cn.jzyunqi.common.third.feishu;

import com.lark.oapi.Client;
import com.lark.oapi.core.Config;
import com.lark.oapi.core.enums.AppType;
import com.lark.oapi.core.enums.BaseUrlEnum;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
@Slf4j
public class FeishuClient {

    @Resource
    private FeishuAuthRepository feishuAuthRepository;

    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<FeishuAuth> feishuAuthList = feishuAuthRepository.getFeishuAuthList();
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
        }else{
            Config config = new Config();
            config.setAppId(feishuAuth.getAppId());
            config.setAppSecret(feishuAuth.getAppSecret());
            config.setBaseUrl(BaseUrlEnum.FeiShu.getUrl());
            config.setAppType(AppType.SELF_BUILT);
            config.setDisableTokenCache(false);
            config.setRequestTimeOut(5);
            config.setTimeOutTimeUnit(TimeUnit.SECONDS);
            config.setLogReqAtDebug(true);
            clientMap.get(feishuAuth.getAppId()).setConfig(config);
        }
    }

    /**
     * 组装飞书应用客户端
     *
     * @param feishuAuth 飞书应用授权信息
     * @return 飞书应用客户端
     */
    private Client getClient(FeishuAuth feishuAuth) {
        try {
            return Client.newBuilder(feishuAuth.getAppId(), feishuAuth.getAppSecret())
                    .requestTimeout(5, TimeUnit.SECONDS)
                    //.disableTokenCache()
                    .logReqAtDebug(true)
                    .build();
        } catch (Exception e) {
            log.error("FeishuWsClient [{}] prepareAndStart error", feishuAuth.getAppId());
            throw e;
        }
    }
}
