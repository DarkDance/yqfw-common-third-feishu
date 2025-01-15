package cn.jzyunqi.common.third.feishu;

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
import java.util.stream.Collectors;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
@Slf4j
public class FeishuWsClient {

    @Resource
    private FeishuClientConfig feishuClientConfig;

    @Autowired(required = false)
    private List<IEventHandler<?>> eventHandlerList;

    @Autowired(required = false)
    private List<ICallBackHandler<?, ?>> callbackHandlerList;

    @PostConstruct
    public void init() {
        List<FeishuAuth> feishuAuthList = feishuClientConfig.getFeishuAuthList();
        for (FeishuAuth feishuAuth : feishuAuthList) {
            if (feishuAuth.getWsConnect()) {
                EventDispatcher.Builder dispatcherBuilder = EventDispatcher.newBuilder(feishuAuth.getVerificationToken(), feishuAuth.getEncryptKey());

                //获取builder的所有方法
                Method[] methods = ReflectionUtils.getDeclaredMethods(dispatcherBuilder.getClass());
                Map<String, Method> methodMap = Arrays.stream(methods).collect(Collectors.toMap(Method::getName, method -> method));

                //设置feishu事件处理
                for (IEventHandler<?> eventHandler : eventHandlerList) {
                    String eventName = eventHandler.getEvent().getClass().getSimpleName();
                    try {
                        methodMap.get("on" + eventName).invoke(dispatcherBuilder, eventHandler);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                //设置feishu回调处理
                for (ICallBackHandler<?, ?> callBackHandler : callbackHandlerList) {
                    String eventName = callBackHandler.getEvent().getClass().getSimpleName();
                    try {
                        methodMap.get("on" + eventName).invoke(dispatcherBuilder, callBackHandler);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                Client wsClient = new Client.Builder(feishuAuth.getAppId(), feishuAuth.getAppSecret())
                        .eventHandler(dispatcherBuilder.build())
                        .build();
                wsClient.start();
                log.info("FeishuWsClient [{}] init success", feishuAuth.getAppId());
            }
        }
    }
}
