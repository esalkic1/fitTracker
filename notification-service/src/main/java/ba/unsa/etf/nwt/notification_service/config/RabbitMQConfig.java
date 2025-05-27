package ba.unsa.etf.nwt.notification_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.ClassMapper;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {
    public static final String USER_EXCHANGE = "user.exchange";
    public static final String NOTIFICATION_USER_CREATED_QUEUE = "notification.user.created.queue";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue notificationUserCreatedQueue() {
        return new Queue(NOTIFICATION_USER_CREATED_QUEUE);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationUserCreatedQueue())
                .to(userExchange())
                .with("user.created");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper());
        return converter;
    }

    @Bean
    public ClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put("ba.unsa.etf.nwt.auth.dto.UserCreatedEvent",
                ba.unsa.etf.nwt.notification_service.dto.UserCreatedEvent.class);
        idClassMapping.put("ba.unsa.etf.nwt.auth.dto.UserCreationFailedEvent",
                ba.unsa.etf.nwt.notification_service.dto.UserCreationFailedEvent.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }
}