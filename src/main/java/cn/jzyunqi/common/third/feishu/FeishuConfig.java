package cn.jzyunqi.common.third.feishu;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wiiyaya
 * @since 2025/1/15
 */
@Configuration
public class FeishuConfig {

    @Bean
    public FeishuClient feishuClient() {
        return new FeishuClient();
    }
}
