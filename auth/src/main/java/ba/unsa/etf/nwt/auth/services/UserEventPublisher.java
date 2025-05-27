package ba.unsa.etf.nwt.auth.services;

import ba.unsa.etf.nwt.auth.config.RabbitMQConfig;
import ba.unsa.etf.nwt.auth.dto.UserCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserCreatedEvent(UserCreatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                "user.created",
                event
        );
    }
}

