package com.yhn.shopping_manager_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ShoppingManagerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingManagerApiApplication.class, args);
    }

}
