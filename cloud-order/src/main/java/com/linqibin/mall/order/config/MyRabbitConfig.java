package com.linqibin.mall.order.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 使用RabbitMQ
 * 1. 引入amqp场景：RabbitAutoConfiguration就会自动生效
 * 2. 给容器中自动配置了
 * 		RabbitTemplate、AmqpAdmin、CachingConnectionFactory、RabbitMessagingTemplate
 * 		所有的属性都是spring.rabbitmq
 * 3. @EnableRabbit 并给配置文件中配置地址相关信息， 只发送消息可以不标注解
 * 4. 监听消息， 使用@RabbitListener: 类+方法上（监听哪些队列即可）
 * 				                      @RabbitHandler: 标在方法上（重载区分不同的消息）
 * 	  参数可以写以下类型
 * 	  1️⃣ Message：原生消息详细信息。消息头+消息题
 * 	  2️⃣ T：发送的消息的类型。
 * 	  3️⃣ Channel：当前传输数据的信道
 * 	Queue可以很多人都来监听。只要收到消息，队列删除消息，而且只有一个收到此消息
 * 		1）服务启动多个，同一个消息，只有一个客户端收到
 * 		2）只有一个消息完全处理完，方法运行结束，就会收到下一个消息。
 * 	5. 定制RabbitTemplate
 * 		1) 服务端收到消息就回调
 * 			- spring.rabbitmq.publisher-confirms=true
 * 			- 设置确认回调ConfirmCallback
 * 		2) 消息未正确抵达队列进行回调
 * 			- spring.rabbitmq.publisher-returns=true
 * 			- spring.rabbitmq.template.mandatory=true
 * 			- 设置回调ReturnCallback
 * 		3) 消费端确认
 * 			- 默认是自动确认的，只要消息被接收到，客户端会自动确认，服务端就会移除这个消息
 * 				问题：我们收到很多消息，自动回复给服务器ack，只有一个消息处理成功此时宕机就会发生消息丢失。
 * 			- 手动确认模式 channel.ack  channel.Nack 可以批量  channel.reject
 * 				spring.rabbitmq.listener.simple.acknowledge-mode=manual
 * RabbitMQ
 * @author linqibin
 * @date   2024/1/7 10:43
 * @email  1214219989@qq.com
 */
@Configuration
public class MyRabbitConfig {

    /**
     * 序列化时使用json
     * @return
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
