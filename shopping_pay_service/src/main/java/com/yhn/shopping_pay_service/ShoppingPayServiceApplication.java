package com.yhn.shopping_pay_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yhn.shopping_pay_service.mapper")
public class ShoppingPayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingPayServiceApplication.class, args);
    }

}
