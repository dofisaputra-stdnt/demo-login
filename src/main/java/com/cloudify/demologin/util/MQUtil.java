package com.cloudify.demologin.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MQUtil {

    private String curr_queue_name;
    private String curr_topic_exchange_name;
    private final RabbitTemplate rabbitTemplate;

    public MQUtil(ConnectionFactory conn_factory) {
        this.rabbitTemplate = new RabbitTemplate(conn_factory);
    }

    public void setQueue(String queue_name, String exchange_name){
        this.curr_queue_name = queue_name;
        this.curr_topic_exchange_name = exchange_name;
    }

    Queue getQueue() {
        return new Queue(this.curr_queue_name, false);
    }

    TopicExchange getTopicExchange() {
        return new TopicExchange(this.curr_topic_exchange_name);
    }

    Binding getBinding(Queue queue, TopicExchange topic_exchange) {
        return BindingBuilder.bind(queue).to(topic_exchange).with(this.curr_queue_name);
    }

    SimpleMessageListenerContainer getContainer(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(this.curr_queue_name);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    MessageListenerAdapter getAdapter() {
        return new MessageListenerAdapter();
    }

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(this.curr_topic_exchange_name, message);
    }

}
