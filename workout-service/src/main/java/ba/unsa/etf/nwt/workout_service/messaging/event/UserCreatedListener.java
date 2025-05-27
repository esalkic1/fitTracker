package ba.unsa.etf.nwt.workout_service.messaging.event;

import ba.unsa.etf.nwt.workout_service.config.RabbitMQConfig;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.dto.UserCreatedEvent;
import ba.unsa.etf.nwt.workout_service.repositories.UserRepository;
import ba.unsa.etf.nwt.workout_service.services.UserEventPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedListener {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;

    public UserCreatedListener(UserRepository userRepository,
                               UserEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @RabbitListener(queues = RabbitMQConfig.WORKOUT_USER_CREATED_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("Workout service received user created event: " + event.getHandle());

        User newUser = new User();
        newUser.setUuid(event.getHandle());

        try {
            userRepository.save(newUser);
            System.out.println("User created in workout service database");
        } catch (DataAccessException e) {
            System.err.println("Error creating user in workout service, triggering rollback...");
            eventPublisher.publishUserCreationFailedEvent(event.getHandle());
        }
    }
}
