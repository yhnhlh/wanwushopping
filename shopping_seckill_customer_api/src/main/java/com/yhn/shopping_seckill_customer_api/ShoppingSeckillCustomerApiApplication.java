package com.yhn.shopping_seckill_customer_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class ShoppingSeckillCustomerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingSeckillCustomerApiApplication.class, args);
    }

}
