package com.yhn.shopping_category_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yhn.shopping_category_service.mapper")
public class ShoppingCategoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCategoryServiceApplication.class, args);
    }

}
