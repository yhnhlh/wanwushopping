package com.yhn.shopping_goods_service;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // 交换机
    private final String GOODS_EXCHANGE = "goods_exchange";
    // 同步商品数据队列
    private final String SYNC_GOODS_QUEUE = "sync_goods_queue";
    // 删除商品数据队列
    private final String DEL_GOODS_QUEUE = "del_goods_queue";

    // 向购物车同步商品队列
    private final String SYNC_CART_QUEUE = "sync_cart_queue";
    // 向购物车删除商品队列
    private final String DEL_CART_QUEUE = "del_cart_queue";

    // 创建交换机
    @Bean(GOODS_EXCHANGE)
    public Exchange getExchange() {
        return ExchangeBuilder
                .topicExchange(GOODS_EXCHANGE) // 交换机类型
                .durable(true) // 是否持久化
                .build();
    }

    // 创建队列
    @Bean(SYNC_GOODS_QUEUE)
    public Queue getQueue1() {
        return new Queue(SYNC_GOODS_QUEUE); // 队列名
    }
    @Bean(DEL_GOODS_QUEUE)
    public Queue getQueue2() {
        return new Queue(DEL_GOODS_QUEUE); // 队列名
    }
    @Bean(SYNC_CART_QUEUE)
    public Queue getQueue3() {
        return new Queue(SYNC_CART_QUEUE); // 队列名
    }

    @Bean(DEL_CART_QUEUE)
    public Queue getQueue4() {
        return new Queue(DEL_CART_QUEUE); // 队列名
    }

    // 交换机绑定队列
    @Bean
    public Binding bindQueue1(@Qualifier(GOODS_EXCHANGE) Exchange exchange,
                              @Qualifier(SYNC_GOODS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.sync_goods.#")
                .noargs();
    }

    @Bean
    public Binding bindQueue2(@Qualifier(GOODS_EXCHANGE) Exchange exchange,
                              @Qualifier(DEL_GOODS_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.del_goods.#")
                .noargs();
    }
    @Bean
    public Binding bindQueue3(@Qualifier(GOODS_EXCHANGE) Exchange exchange,
                              @Qualifier(SYNC_CART_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.sync_cart.#")
                .noargs();
    }
    @Bean
    public Binding bindQueue4(@Qualifier(GOODS_EXCHANGE) Exchange exchange,
                              @Qualifier(DEL_CART_QUEUE) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with("#.del_cart.#")
                .noargs();
    }
}
