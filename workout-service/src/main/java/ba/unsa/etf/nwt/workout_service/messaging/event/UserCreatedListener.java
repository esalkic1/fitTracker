package ba.unsa.etf.nwt.workout_service.messaging.event;

import ba.unsa.etf.nwt.workout_service.config.RabbitMQConfig;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.dto.UserCreatedEvent;
import ba.unsa.etf.nwt.workout_service.repositories.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedListener {

    private final UserRepository userRepository;

    public UserCreatedListener(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.WORKOUT_USER_CREATED_QUEUE)
    public void handleUserCreated(UserCreatedEvent event) {
        System.out.println("Workout service received user created event: " + event.getHandle());

        User newUser = new User();
        //newUser.setId(event.getUserId()); // id generated locally
        newUser.setUuid(event.getHandle());

        userRepository.save(newUser);
        System.out.println("User created in workout service database");
    }
}