package com.yhn.shopping_order_customer_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingOrderCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingOrderCustomerApiApplication.class, args);
    }

}
