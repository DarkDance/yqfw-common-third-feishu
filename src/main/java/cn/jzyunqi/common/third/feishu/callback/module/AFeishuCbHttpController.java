package cn.jzyunqi.common.third.feishu.callback.module;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.third.feishu.FeishuClientConfig;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import com.lark.oapi.core.utils.Jsons;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.Map;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
@Slf4j
public abstract class AFeishuCbHttpController {

    @Resource
    private FeishuClientConfig feishuClientConfig;

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
    public EventCbData feishuEventCallback(@RequestBody EventCbData eventCbData, @RequestBody(required = false) String eventCbDataStr, @RequestHeader Map<String, String> headers
    ) throws BusinessException {
        log.debug("""
                        
                        ======Request Header    : {}
                        ======Request BodyStr   : {}
                        """,
                headers,
                eventCbDataStr
        );
        if (StringUtilPlus.isNotEmpty(eventCbData.getEncrypt())) {
            String encryptKey = feishuClientConfig.getFeishuAuth(getAppId()).getEncryptKey();
            //签名验证
            String timestamp = headers.get("X-Lark-Request-Timestamp");
            String nonce = headers.get("X-Lark-Request-Nonce");
            String headerSign = headers.get("X-Lark-Signature");
            if(StringUtilPlus.isNotEmpty(headerSign)){
                String sign = DigestUtilPlus.SHA.sign(timestamp + nonce + encryptKey + eventCbDataStr, DigestUtilPlus.SHAAlgo._256, Boolean.FALSE);
                if(!headerSign.equals(sign)){
                    throw new BusinessException("签名验证失败");
                }
            }

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
        return eventCbData;
    }

    protected abstract String getAppId();
}
