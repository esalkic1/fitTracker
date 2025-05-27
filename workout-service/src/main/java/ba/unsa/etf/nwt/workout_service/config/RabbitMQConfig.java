package ba.unsa.etf.nwt.workout_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
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

    @Bean
    public TopicExchange userExchange() {
        return new TopicExchange(USER_EXCHANGE);
    }

    @Bean
    public Queue workoutUserCreatedQueue() {
        return new Queue(WORKOUT_USER_CREATED_QUEUE);
    }

    @Bean
    public Binding workoutBinding() {
        return BindingBuilder
                .bind(workoutUserCreatedQueue())
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
                ba.unsa.etf.nwt.workout_service.dto.UserCreatedEvent.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }
}