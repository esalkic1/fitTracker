package ba.unsa.etf.nwt.notification_service.messaging.event;

import ba.unsa.etf.nwt.notification_service.config.RabbitMQConfig;
import ba.unsa.etf.nwt.notification_service.domain.User;
import ba.unsa.etf.nwt.notification_service.dto.UserCreatedEvent;
import ba.unsa.etf.nwt.notification_service.repositories.UserRepository;
import ba.unsa.etf.nwt.notification_service.services.UserEventPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedListener {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;

    public UserCreatedListener(UserRepository userRepository, UserEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_USER_CREATED_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("Notification service received user created event: " + event.getHandle() + " mail: " + event.getEmail());

        User newUser = new User();
        //newUser.setId(event.getUserId()); // id generated locally
        newUser.setHandle(event.getHandle());
        newUser.setEmail(event.getEmail());

        try {
            userRepository.save(newUser);
            System.out.println("User created in notification service database");
        } catch (DataAccessException e) {
            System.err.println("Error creating user in notification service, triggering rollback...");
            eventPublisher.publishUserCreationFailedEvent(event.getHandle());
        }
    }
}