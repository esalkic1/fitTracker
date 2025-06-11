package ba.unsa.etf.nwt.auth.messaging.event;

import ba.unsa.etf.nwt.auth.config.RabbitMQConfig;
import ba.unsa.etf.nwt.auth.dto.UserCreationFailedEvent;
import ba.unsa.etf.nwt.auth.repositories.UserRepository;
import ba.unsa.etf.nwt.auth.services.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserRollbackListener {

    private final UserService userService;

    public UserRollbackListener(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = RabbitMQConfig.USER_CREATION_FAILED_QUEUE)
    public void handleUserCreationFailed(UserCreationFailedEvent event) {
        System.out.println("Rollback triggered for handle: " + event.getHandle());
        userService.delete(event.getHandle());
    }
}

