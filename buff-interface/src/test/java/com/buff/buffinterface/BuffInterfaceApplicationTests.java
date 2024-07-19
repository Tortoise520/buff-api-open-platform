package com.buff.buffinterface;

import com.buff.buffclientsdk.client.BuffApiClient;
import com.buff.buffclientsdk.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BuffInterfaceApplicationTests {
    @Autowired
    BuffApiClient buffApiClient ;
    @Test
    void contextLoads() {
        String result = buffApiClient.getNameByGet("buff");
        System.out.println(result);
        User user = new User();
        user.setUsername("buff");
        result = buffApiClient.getUserNameByPost(user);
        System.out.println(result);
    }
    
}
