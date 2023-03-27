package com.yhn.shopping_message_service.service;

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.yhn.shopping_common.result.BaseResult;
import com.yhn.shopping_common.service.MessageService;
import lombok.SneakyThrows;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@DubboService
@Service
public class MessageServiceImpl implements MessageService {
    @Value("${message.accessKeyId}")
    private String accessKeyId;
    @Value("${message.accessKeySecret}")
    private String accessKeySecret;
    @SneakyThrows
    @Override
    public BaseResult sendMessage(String phoneNumber, String code) {
        // 工程代码泄露可能会导致AccessKey泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        com.aliyun.dysmsapi20170525.Client client = createClient(accessKeyId, accessKeySecret);
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName("阿里云短信测试")
                .setTemplateCode("SMS_154950909")
                .setPhoneNumbers(phoneNumber)
                .setTemplateParam("{\"code\":\""+code+"\"}");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

            // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse sendSmsResponse = client.sendSmsWithOptions(sendSmsRequest, runtime);
        SendSmsResponseBody body = sendSmsResponse.getBody();
        if ("OK".equals(body.getCode())){
            return new BaseResult(200,body.getCode(),body.getMessage());
        }else{
            return new BaseResult(500,body.getCode(),body.getMessage());
        }

    }
    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    @SneakyThrows
    public  com.aliyun.dysmsapi20170525.Client createClient(String accessKeyId, String accessKeySecret)  {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

}
