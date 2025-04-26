package cn.jzyunqi.common.third.feishu;

import cn.jzyunqi.common.utils.StringUtilPlus;

import java.util.List;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
public interface FeishuAuthRepository {

    List<FeishuAuth> getFeishuAuthList();
}
