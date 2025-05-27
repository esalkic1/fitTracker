package ba.unsa.etf.nwt.nutrition_service.messaging.event;

import ba.unsa.etf.nwt.nutrition_service.config.RabbitMQConfig;
import ba.unsa.etf.nwt.nutrition_service.dto.UserCreationFailedEvent;
import ba.unsa.etf.nwt.nutrition_service.repositories.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserRollbackListener {

    private final UserRepository userRepository;

    public UserRollbackListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_CREATION_FAILED_QUEUE)
    public void handleUserCreationFailed(UserCreationFailedEvent event) {
        System.out.println("Rollback triggered for handle: " + event.getHandle());

        userRepository.findByUuid(event.getHandle()).ifPresentOrElse(
                userRepository::delete,
                () -> System.err.println("User not found for UUID: " + event.getHandle())
        );
    }

}

