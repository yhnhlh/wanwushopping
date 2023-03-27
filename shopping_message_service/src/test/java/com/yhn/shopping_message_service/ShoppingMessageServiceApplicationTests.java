package com.yhn.shopping_message_service;

import com.yhn.shopping_common.result.BaseResult;
import com.yhn.shopping_common.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ShoppingMessageServiceApplicationTests {

    @Autowired
    MessageService messageService;

    @Test
    void contextLoads()  {
        BaseResult baseResult = messageService.sendMessage("18461009108", "9999");
        System.out.println(baseResult);
    }

}
