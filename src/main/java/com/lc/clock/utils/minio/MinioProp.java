package com.lc.clock.utils.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author 柴柴快乐每一天
 * @create 2021-08-12  5:14 下午
 * <p>
 * 『Stay hungry, stay foolish. 』
 */

@Data
@ConfigurationProperties(prefix = "minio")
@Component
public class MinioProp {
    private String endpoint;
    private String host;
    private String accessKey;
    private String secretKey;
}
