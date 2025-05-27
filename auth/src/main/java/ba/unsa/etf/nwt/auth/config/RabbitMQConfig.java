package ba.unsa.etf.nwt.auth.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    public static final String WORKOUT_USER_CREATED_QUEUE = "workout.user.created.queue";
    public static final String NUTRITION_USER_CREATED_QUEUE = "nutrition.user.created.queue";
    public static final String NOTIFICATION_USER_CREATED_QUEUE = "notification.user.created.queue";
    public static final String USER_CREATED_ROUTING_KEY = "user.created";

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    // Workout service queue
    @Bean
    public Queue workoutUserCreatedQueue() {
        return new Queue(WORKOUT_USER_CREATED_QUEUE);
    }

    // Nutrition service queue
    @Bean
    public Queue nutritionUserCreatedQueue() {
        return new Queue(NUTRITION_USER_CREATED_QUEUE);
    }

    @Bean
    public Queue notificationUserCreatedQueue() {
        return new Queue(NOTIFICATION_USER_CREATED_QUEUE);
    }

    // Binding for workout service
    @Bean
    public Binding workoutBinding() {
        return BindingBuilder
                .bind(workoutUserCreatedQueue())
                .to(userExchange())
                .with(USER_CREATED_ROUTING_KEY);
    }

    // Binding for nutrition service
    @Bean
    public Binding nutritionBinding() {
        return BindingBuilder
                .bind(nutritionUserCreatedQueue())
                .to(userExchange())
                .with(USER_CREATED_ROUTING_KEY);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder
                .bind(notificationUserCreatedQueue())
                .to(userExchange())
                .with(USER_CREATED_ROUTING_KEY);
    }

//    @Bean
//    public Jackson2JsonMessageConverter messageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
//                                         Jackson2JsonMessageConverter messageConverter) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(messageConverter);
//        return rabbitTemplate;
//    }

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
                ba.unsa.etf.nwt.auth.dto.UserCreatedEvent.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}