package com.yhn.shopping_cart_customer_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingCartCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingCartCustomerApiApplication.class, args);
    }

}
