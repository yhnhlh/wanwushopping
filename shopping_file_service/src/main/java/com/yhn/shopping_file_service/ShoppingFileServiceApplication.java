package com.yhn.shopping_file_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingFileServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingFileServiceApplication.class, args);
    }

}
