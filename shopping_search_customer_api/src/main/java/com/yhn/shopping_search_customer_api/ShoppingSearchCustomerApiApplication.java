package com.yhn.shopping_search_customer_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingSearchCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingSearchCustomerApiApplication.class, args);
    }

}
