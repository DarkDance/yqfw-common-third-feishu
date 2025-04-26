package cn.jzyunqi.common.third.feishu.callback;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.third.feishu.FeishuAuth;
import cn.jzyunqi.common.third.feishu.FeishuAuthRepository;
import cn.jzyunqi.common.third.feishu.callback.module.EventCbData;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import com.lark.oapi.core.utils.Jsons;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.event.ICallBackHandler;
import com.lark.oapi.event.IEventHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
@Slf4j
public abstract class AFeishuCbHttpController {

    @Resource
    private FeishuAuthRepository feishuAuthRepository;

    @Autowired(required = false)
    private List<IEventHandler<?>> eventHandlerList;

    @Autowired(required = false)
    private List<ICallBackHandler<?, ?>> callbackHandlerList;

    private EventDispatcher eventDispatcher;

    @PostConstruct
    public void init() {
        FeishuAuth feishuAuth = feishuAuthRepository.chooseFeishuAuth(getAppId());
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
        eventDispatcher = dispatcherBuilder.build();
    }

    /**
     * 飞书事件与回调处理
     *
     * @param eventCbData    事件数据
     * @param eventCbDataStr 事件数据字符串
     * @param headers        请求头
     * @return 事件数据
     */
    @RequestMapping
    @ResponseBody
    public Object feishuEventCallback(@RequestBody EventCbData eventCbData, @RequestBody(required = false) String eventCbDataStr, @RequestHeader Map<String, String> headers
    ) throws BusinessException {
        log.debug("""
                        
                        ======Request Header    : {}
                        ======Request BodyStr   : {}
                        """,
                headers,
                eventCbDataStr
        );
        FeishuAuth feishuAuth = feishuAuthRepository.chooseFeishuAuth(getAppId());
        String encryptKey = feishuAuth.getEncryptKey();
        if (StringUtilPlus.isNotEmpty(eventCbData.getEncrypt())) {
            //解密
            String key = DigestUtilPlus.SHA.sign(encryptKey, DigestUtilPlus.SHAAlgo._256, Boolean.TRUE);
            byte[] decode = DigestUtilPlus.Base64.decodeBase64(eventCbData.getEncrypt());
            byte[] iv = Arrays.copyOfRange(decode, 0, 16);
            byte[] contentByte = Arrays.copyOfRange(decode, 16, decode.length);
            try {
                String content = DigestUtilPlus.AES.decryptCBCNoPadding(contentByte, DigestUtilPlus.Base64.decodeBase64(key), iv);
                eventCbData = Jsons.DEFAULT.fromJson(content, EventCbData.class);
            } catch (Exception e) {
                throw new BusinessException("解密失败");
            }
        }
        // 服务有效性验证
        if ("url_verification".equals(eventCbData.getType())) {
            EventCbData responseData = new EventCbData();
            responseData.setChallenge(eventCbData.getChallenge());
            return responseData;
        }

        //签名验证
        String timestamp = headers.get("X-Lark-Request-Timestamp");
        String nonce = headers.get("X-Lark-Request-Nonce");
        String headerSign = headers.get("X-Lark-Signature");
        if (StringUtilPlus.isNotEmpty(headerSign)) {
            String sign = DigestUtilPlus.SHA.sign(timestamp + nonce + encryptKey + eventCbDataStr, DigestUtilPlus.SHAAlgo._256, Boolean.FALSE);
            if (!headerSign.equals(sign)) {
                throw new BusinessException("签名验证失败");
            }
        }

        try {
            return eventDispatcher.doWithoutValidation(eventCbDataStr.getBytes(StandardCharsets.UTF_8));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String getAppId();
}
