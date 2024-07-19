package com.buff.buffinterface;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(value = com.buff.buffclientsdk.BuffApiClientConfig.class)//解决无法自动注入BuffApiClientConfig和BuffApiClient对象的问题
@SpringBootApplication
public class BuffInterfaceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(BuffInterfaceApplication.class, args);
    }
    
}
