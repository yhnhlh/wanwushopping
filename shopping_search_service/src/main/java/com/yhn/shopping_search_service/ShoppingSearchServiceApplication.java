package com.yhn.shopping_search_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingSearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingSearchServiceApplication.class, args);
    }

}
