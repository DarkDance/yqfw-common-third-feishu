package cn.jzyunqi.common.third.feishu;

import cn.jzyunqi.common.utils.CollectionUtilPlus;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.event.ICallBackHandler;
import com.lark.oapi.event.IEventHandler;
import com.lark.oapi.ws.Client;
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
import java.util.stream.Collectors;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
@Slf4j
public class FeishuWsClient {

    @Resource
    private FeishuAuthHelper feishuAuthHelper;

    @Autowired(required = false)
    private List<IEventHandler<?>> eventHandlerList;

    @Autowired(required = false)
    private List<ICallBackHandler<?, ?>> callbackHandlerList;

    private final Map<String, Client> clientMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        List<FeishuAuth> feishuAuthList = feishuAuthHelper.getFeishuAuthList();
        for (FeishuAuth feishuAuth : feishuAuthList) {
            if (feishuAuth.getWsConnect()) {
                clientMap.put(feishuAuth.getAppId(), prepareAndStart(feishuAuth));
            }
        }
    }

    /**
     * 添加一个新的飞书WS应用
     *
     * @param feishuAuth 飞书应用授权信息
     */
    public void addWsClient(FeishuAuth feishuAuth) {
        if (!clientMap.containsKey(feishuAuth.getAppId())) {
            clientMap.put(feishuAuth.getAppId(), prepareAndStart(feishuAuth));
        }
        log.info("FeishuWsClient [{}] addWsClient success", feishuAuth.getAppId());
    }

    /**
     * 组装并启动飞书WS客户端
     *
     * @param feishuAuth 飞书应用授权信息
     */
    private Client prepareAndStart(FeishuAuth feishuAuth) {
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
            Client wsClient = new Client.Builder(feishuAuth.getAppId(), feishuAuth.getAppSecret())
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
