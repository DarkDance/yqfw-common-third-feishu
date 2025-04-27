package cn.jzyunqi.common.third.feishu;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
public abstract class FeishuAuthRepository implements InitializingBean {

    private final Map<String, FeishuAuth> authMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<FeishuAuth> feishuAuthList = initFeishuAuthList();
        for (FeishuAuth feishuAuth : feishuAuthList) {
            authMap.put(feishuAuth.getAppId(), feishuAuth);
        }
    }

    public FeishuAuth chooseFeishuAuth(String feishuAuthId) {
        return authMap.get(feishuAuthId);
    }

    public void addFeishuAuth(FeishuAuth feishuAuth) {
        if (!authMap.containsKey(feishuAuth.getAppId())) {
            authMap.put(feishuAuth.getAppId(), feishuAuth);
        }
    }

    public void removeFeishuAuth(String feishuAuthId) {
        authMap.remove(feishuAuthId);
    }

    public List<FeishuAuth> getFeishuAuthList() {
        return new ArrayList<>(authMap.values());
    }

    public abstract List<FeishuAuth> initFeishuAuthList();
}
