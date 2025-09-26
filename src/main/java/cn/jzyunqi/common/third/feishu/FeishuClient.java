package cn.jzyunqi.common.third.feishu;

import cn.jzyunqi.common.utils.CollectionUtilPlus;
import com.lark.oapi.Client;
import com.lark.oapi.core.Config;
import com.lark.oapi.core.enums.AppType;
import com.lark.oapi.core.enums.BaseUrlEnum;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.event.ICallBackHandler;
import com.lark.oapi.event.IEventHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
@Slf4j
public class FeishuClient {

    @Resource
    private FeishuAuthHelper feishuAuthHelper;

    @Autowired(required = false)
    private List<IEventHandler<?>> eventHandlerList;

    @Autowired(required = false)
    private List<ICallBackHandler<?, ?>> callbackHandlerList;

    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    private final Map<String, com.lark.oapi.ws.Client> wsClientMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<FeishuAuth> feishuAuthList = this.feishuAuthHelper.getFeishuAuthList();
        for (FeishuAuth feishuAuth : feishuAuthList) {
            clientMap.put(feishuAuth.getAppId(), getClient(feishuAuth));
            if (feishuAuth.getWsConnect()) {
                wsClientMap.put(feishuAuth.getAppId(), prepareAndStart(feishuAuth));
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
            wsClientMap.put(feishuAuth.getAppId(), prepareAndStart(feishuAuth));
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

    /**
     * 组装并启动飞书WS客户端
     *
     * @param feishuAuth 飞书应用授权信息
     */
    private com.lark.oapi.ws.Client prepareAndStart(FeishuAuth feishuAuth) {
        try {
            EventDispatcher.Builder dispatcherBuilder = EventDispatcher.newBuilder(feishuAuth.getVerificationToken(), feishuAuth.getEncryptKey());

            //获取builder的所有方法
            Method[] methods = ReflectionUtils.getDeclaredMethods(dispatcherBuilder.getClass());
            Map<String, Method> methodMap = Arrays.stream(methods).collect(Collectors.toMap(Method::getName, method -> method));

            //设置feishu事件处理
            if (CollectionUtilPlus.Collection.isNotEmpty(eventHandlerList)) {
                for (IEventHandler<?> eventHandler : eventHandlerList) {
                    String eventName = eventHandler.getEvent().getClass().getSimpleName();
                    try {
                        methodMap.get("on" + eventName).invoke(dispatcherBuilder, eventHandler);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            //设置feishu回调处理
            if (CollectionUtilPlus.Collection.isNotEmpty(callbackHandlerList)) {
                for (ICallBackHandler<?, ?> callBackHandler : callbackHandlerList) {
                    String eventName = callBackHandler.getEvent().getClass().getSimpleName();
                    try {
                        methodMap.get("on" + eventName).invoke(dispatcherBuilder, callBackHandler);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            com.lark.oapi.ws.Client wsClient = new com.lark.oapi.ws.Client.Builder(feishuAuth.getAppId(), feishuAuth.getAppSecret())
                    .eventHandler(dispatcherBuilder.build())
                    .build();
            wsClient.start();
            log.info("FeishuWsClient [{}] init success", feishuAuth.getAppId());
            return wsClient;
        } catch (Exception e) {
            log.error("FeishuWsClient [{}] init error", feishuAuth.getAppId());
            throw e;
        }
    }
}
