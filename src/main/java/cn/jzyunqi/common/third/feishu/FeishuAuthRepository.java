package cn.jzyunqi.common.third.feishu;

import cn.jzyunqi.common.utils.StringUtilPlus;

import java.util.List;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
public interface FeishuAuthRepository {

    List<FeishuAuth> getFeishuAuthList();

    default FeishuAuth getFeishuAuth(String appId) {
        if (StringUtilPlus.isEmpty(appId)) {
            return getFeishuAuthList().stream().findFirst().orElse(new FeishuAuth());
        } else {
            return getFeishuAuthList().stream().filter(authInfo -> authInfo.getAppId().equals(appId)).findFirst().orElse(new FeishuAuth());
        }
    }
}
