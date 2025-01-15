### 接入包说明
本接入包提供了dify接口的接入方法。

* 引入依赖包
```xml
<dependency>
    <groupId>cn.jzyunqi</groupId>
    <artifactId>yqfw-common-third-feishu</artifactId>
    <version>${yqfw.version}</version>
</dependency>
```
* 引入配置
```java
@Import({FeishuConfig.class})
```
* 配置自己的飞书信息
```java
@Bean
public FeishuClientConfig feishuClientConfig() {
    return () -> List.of(
            new FeishuAuth("xxx", "yyy"),
            new FeishuAuth("eee", "zzz")
    );
}
```