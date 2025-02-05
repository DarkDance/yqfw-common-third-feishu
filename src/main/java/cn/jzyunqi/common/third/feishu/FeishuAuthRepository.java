package cn.jzyunqi.common.third.feishu;

import java.util.List;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
public interface FeishuAuthRepository {

    List<FeishuAuth> getFeishuAuthList();

    default FeishuAuth getFeishuAuth(String appId) {
        return getFeishuAuthList().stream().filter(authInfo -> authInfo.getAppId().equals(appId)).findFirst().orElse(new FeishuAuth());
    }
}
