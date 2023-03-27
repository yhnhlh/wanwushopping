package com.yhn.shopping_message_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingMessageServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingMessageServiceApplication.class, args);
    }

}
