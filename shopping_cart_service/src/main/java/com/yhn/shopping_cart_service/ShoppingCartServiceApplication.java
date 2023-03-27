package com.yhn.shopping_cart_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingCartServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartServiceApplication.class, args);
    }

}
