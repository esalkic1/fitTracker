package ba.unsa.etf.nwt.notification_service.services;

import ba.unsa.etf.nwt.notification_service.config.RabbitMQConfig;
import ba.unsa.etf.nwt.notification_service.dto.UserCreationFailedEvent;
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
