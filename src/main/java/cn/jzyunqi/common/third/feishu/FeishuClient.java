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
    private FeishuAuthHelper feishuAuthHelper;

    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<FeishuAuth> feishuAuthList = feishuAuthHelper.getFeishuAuthList();
        for (FeishuAuth feishuAuth : feishuAuthList) {
            if(!clientMap.containsKey(feishuAuth.getAppId())){
                clientMap.put(feishuAuth.getAppId(), getClient(feishuAuth));
            }else{
                log.info("FeishuClient [{}] already init", feishuAuth.getAppId());
            }
        }
    }

    /**
     * 选择一个飞书应用
     *
     * @param feishuAuthId 飞书应用ID
     * @return 飞书应用
     */
    public Client chooseClient(String feishuAuthId) {
        if (!clientMap.containsKey(feishuAuthId)) {
            FeishuAuth feishuAuth = feishuAuthHelper.chooseFeishuAuth(feishuAuthId);
            addClient(feishuAuth);
        }
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
        } else {
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
            log.info("FeishuClient [{}] config change success", feishuAuth.getAppId());
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
            Client client = Client.newBuilder(feishuAuth.getAppId(), feishuAuth.getAppSecret())
                    .requestTimeout(5, TimeUnit.SECONDS)
                    //.disableTokenCache()
                    .logReqAtDebug(true)
                    .build();
            log.info("FeishuClient [{}] init success", feishuAuth.getAppId());
            return client;
        } catch (Exception e) {
            log.error("FeishuClient [{}] init error", feishuAuth.getAppId());
            throw e;
        }
    }
}
