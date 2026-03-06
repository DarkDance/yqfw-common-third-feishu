### 一、项目介绍
yqfw-common-third-feishu是云祺框架的飞书开放平台接入模块，基于飞书官方OAPI SDK封装，提供了飞书消息发送、文档操作、通讯录管理、机器人回调等功能的统一接入。该模块支持飞书即时消息、云文档、日历、视频会议等多种服务的API调用，同时提供了WebSocket客户端用于实时消息推送。

### 二、项目结构
本模块遵循统一的类命名规范：
- `FeishuAuth.java` —— 认证信息（App ID、App Secret等）
- `FeishuAuthHelper.java` —— 认证助手（由业务侧实现，提供认证信息）
- `FeishuClient.java` —— 客户端（对外暴露服务调用入口）
- `FeishuConfig.java` —— 配置类（注册Bean到Spring容器）

```
yqfw-common-third-feishu
└── src/main/java
    ├── cn.jzyunqi.common.third.feishu
    │   └── callback
    │       └── module
    └── com.lark.oapi.service
        ├── bitable.v1
        │   ├── enums
        │   └── model
        └── im.v1
            └── model.ext
│   │   │                  ├── MessageFilePatch.java #文件消息补丁
│   │   │                  └── MessagePostMdPatch.java #富文本消息补丁
```

### 三、使用说明

#### 1. 安装依赖
运行mvn clean install命令安装当前包，然后在个人项目中引入如下依赖：
```xml
<dependency>
    <groupId>cn.jzyunqi</groupId>
    <artifactId>yqfw-common-third-feishu</artifactId>
    <version>${yqfw.version}</version>
</dependency>
```

#### 2. 配置飞书服务
在个人项目中引入飞书配置：
```java
@Import({FeishuConfig.class})
```

配置飞书认证信息（支持多个飞书应用）：
```java
@Bean
public FeishuAuthRepository feishuAuthRepository() {
    return () -> List.of(
            new FeishuAuth("your-app-id-1", "your-app-secret-1"),
            new FeishuAuth("your-app-id-2", "your-app-secret-2")
    );
}
```

#### 3. 使用飞书客户端发送消息
注入FeishuClient并调用消息API：
```java
@Resource
private FeishuClient feishuClient;

public void sendMessage() throws Exception {
    // 发送文本消息
    SendMessageReq sendReq = SendMessageReq.newBuilder()
            .receiveIdType(ReceiveIdType.OPEN_ID)
            .receiveId("ou_xxxxx")  // 用户OpenID
            .msgType(MsgType.TEXT)
            .content("{\"text\":\"你好，这是一条测试消息\"}")
            .build();
    
    SendMessageResp resp = feishuClient.im().message().send(sendReq);
    
    if (resp.success()) {
        System.out.println("消息发送成功，消息ID: " + resp.getData().getMessageId());
    }
}
```

#### 4. 发送富文本消息
发送包含图片、链接等内容的富文本消息：
```java
@Resource
private FeishuClient feishuClient;

public void sendPostMessage() throws Exception {
    // 构造富文本内容
    String content = "{\n" +
            "  \"zh_cn\": {\n" +
            "    \"title\": \"通知标题\",\n" +
            "    \"content\": [[{\n" +
            "      \"tag\": \"text\",\n" +
            "      \"text\": \"这是一条重要通知\"\n" +
            "    }]]\n" +
            "  }\n" +
            "}";
    
    SendMessageReq sendReq = SendMessageReq.newBuilder()
            .receiveIdType(ReceiveIdType.CHAT_ID)
            .receiveId("oc_xxxxx")  // 群组ID
            .msgType(MsgType.POST)
            .content(content)
            .build();
    
    SendMessageResp resp = feishuClient.im().message().send(sendReq);
}
```

#### 5. 接入飞书事件回调
创建一个Controller继承AFeishuCbHttpController来处理飞书事件回调：

```java
@RestController
@RequestMapping("/api/feishu/callback")
public class FeishuCbController extends AFeishuCbHttpController {
    
    @Override
    protected String processEvent(EventCbData eventCbData) throws BusinessException {
        EventHeaderData header = eventCbData.getHeader();
        
        // 处理不同类型的事件
        switch (header.getEventType()) {
            case "im.message.receive_v1":
                // 处理接收消息事件
                System.out.println("收到消息: " + eventCbData.getEvent());
                break;
            case "contact.user.created_v3":
                // 处理用户创建事件
                System.out.println("新用户创建");
                break;
            default:
                System.out.println("未处理的事件类型: " + header.getEventType());
        }
        
        return "success";
    }
    
    @Override
    protected String getAppId() {
        return "your-app-id";
    }
}
```

在飞书开放平台配置回调地址为：`https://your-domain.com/api/feishu/callback`

#### 6. 使用飞书WebSocket客户端
创建WebSocket客户端接收实时消息推送：

```java
@Resource
private FeishuWsClient feishuWsClient;

public void connectWebSocket() {
    // 连接飞书WebSocket
    feishuWsClient.connect("your-app-id", new FeishuWsHandler() {
        @Override
        public void onMessage(String message) {
            System.out.println("收到实时消息: " + message);
        }
        
        @Override
        public void onError(Throwable error) {
            System.err.println("WebSocket错误: " + error.getMessage());
        }
    });
}
```

#### 7. 操作云文档
使用飞书客户端操作云文档：

```java
@Resource
private FeishuClient feishuClient;

public void manageDocument() throws Exception {
    // 创建飞书文档
    CreateDocumentReq createReq = CreateDocumentReq.newBuilder()
            .folderToken("fldxxxxx")  // 文件夹Token
            .title("新建文档")
            .build();
    
    CreateDocumentResp resp = feishuClient.docx().document().create(createReq);
    
    if (resp.success()) {
        System.out.println("文档创建成功，文档Token: " + resp.getData().getDocument().getDocumentId());
    }
}
```

#### 8. 管理通讯录
获取和管理飞书通讯录信息：

```java
@Resource
private FeishuClient feishuClient;

public void getUserInfo() throws Exception {
    // 获取用户信息
    GetUserReq getUserReq = GetUserReq.newBuilder()
            .userId("ou_xxxxx")
            .userIdType(UserIdType.OPEN_ID)
            .build();
    
    GetUserResp resp = feishuClient.contact().user().get(getUserReq);
    
    if (resp.success()) {
        User user = resp.getData().getUser();
        System.out.println("用户名: " + user.getName());
        System.out.println("邮箱: " + user.getEmail());
    }
}
```