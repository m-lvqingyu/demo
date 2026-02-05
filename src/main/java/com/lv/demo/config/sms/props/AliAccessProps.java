package com.lv.demo.config.sms.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lv
 */
@Data
@Component
@ConfigurationProperties(prefix = "ali.access")
public class AliAccessProps {

    private String key;

    private String secret;

}
