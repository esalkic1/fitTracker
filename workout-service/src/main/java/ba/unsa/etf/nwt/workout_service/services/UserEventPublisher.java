package ba.unsa.etf.nwt.workout_service.services;

import ba.unsa.etf.nwt.workout_service.config.RabbitMQConfig;
import ba.unsa.etf.nwt.workout_service.dto.UserCreationFailedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public UserEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserCreationFailedEvent(UUID handle) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.USER_EXCHANGE,
                "user.creation.failed",
                new UserCreationFailedEvent(handle)
        );
    }
}
