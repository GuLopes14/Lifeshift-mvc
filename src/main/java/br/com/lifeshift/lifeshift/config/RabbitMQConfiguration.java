package br.com.lifeshift.lifeshift.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {

    public static final String PLANO_QUEUE = "lifeshift.plano.queue";
    public static final String PLANO_EXCHANGE = "lifeshift.plano.exchange";
    public static final String PLANO_ROUTING_KEY = "lifeshift.plano.routing.key";

    @Bean
    public Queue planoQueue() {
        return QueueBuilder.durable(PLANO_QUEUE)
                .withArgument("x-dead-letter-exchange", PLANO_EXCHANGE + ".dlx")
                .build();
    }

    @Bean
    public TopicExchange planoExchange() {
        return new TopicExchange(PLANO_EXCHANGE);
    }

    @Bean
    public Binding planoBinding(Queue planoQueue, TopicExchange planoExchange) {
        return BindingBuilder
                .bind(planoQueue)
                .to(planoExchange)
                .with(PLANO_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
