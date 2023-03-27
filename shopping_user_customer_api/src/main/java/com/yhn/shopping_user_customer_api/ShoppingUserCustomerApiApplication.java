package com.yhn.shopping_user_customer_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingUserCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingUserCustomerApiApplication.class, args);
    }

}
