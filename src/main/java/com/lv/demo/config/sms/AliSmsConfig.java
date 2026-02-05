package com.lv.demo.config.sms;

import com.aliyun.credentials.Client;
import com.aliyun.credentials.models.Config;
import com.lv.demo.config.sms.props.AliAccessProps;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lv
 */
@RequiredArgsConstructor
@Configuration
public class AliSmsConfig {

    private static final String ALI_AK_TYPE = "access_key";

    /**
     * Endpoint 请参考 <a href="https://api.aliyun.com/product/Dysmsapi">...</a>
     */
    private static final String DEF_ENDPOINT = "dysmsapi.aliyuncs.com";

    private final AliAccessProps aliAccessProps;

    @Bean(name = "smsClient")
    public com.aliyun.dysmsapi20170525.Client smsClient() throws Exception {
        // 工程代码建议使用更安全的无AK方式，凭据配置方式请参见：https://help.aliyun.com/document_detail/378657.html。
        Config credentialConfig = new Config();
        credentialConfig.setType(ALI_AK_TYPE);
        credentialConfig.setAccessKeyId(aliAccessProps.getKey());
        credentialConfig.setAccessKeySecret(aliAccessProps.getSecret());
        Client credentialClient = new Client(credentialConfig);
        com.aliyun.teaopenapi.models.Config config =
                new com.aliyun.teaopenapi.models.Config().setCredential(credentialClient);
        config.endpoint = DEF_ENDPOINT;
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

}
