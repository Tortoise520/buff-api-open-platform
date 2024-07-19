package com.buff.buffclientsdk;

import com.buff.buffclientsdk.client.BuffApiClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan
@Configuration
@ConfigurationProperties(prefix = "buff.client")
@Data
public class BuffApiClientConfig {
    private String accessKey;
    private String secretKey;
    
    @Bean
    public BuffApiClient buffApiClient() {
        return new BuffApiClient(accessKey, secretKey);
    }
    
}
