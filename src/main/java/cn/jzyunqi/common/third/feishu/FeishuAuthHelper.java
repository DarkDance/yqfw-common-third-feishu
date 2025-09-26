package cn.jzyunqi.common.third.feishu;

import java.util.List;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
public interface FeishuAuthHelper {

    List<FeishuAuth> getFeishuAuthList();

    FeishuAuth chooseFeishuAuth(String feishuAuthId);
}
