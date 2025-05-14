package ba.unsa.etf.nwt.workout_service.n_plus_one;

import ba.unsa.etf.nwt.workout_service.HibernateStatisticsLogger;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class UserNPlusOneTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HibernateStatisticsLogger statisticsLogger;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        Session session = entityManager.unwrap(Session.class);
        session.getSessionFactory().getStatistics().clear();

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void whenAccessWorkoutsAfterFindAll_thenTriggersNPlusOne() {
        statisticsLogger.logStatistics();

        List<User> users = userRepository.findAll();

        statisticsLogger.logStatistics();

        for (User user : users) {
            System.out.println("User " + user.getId() + " has " + user.getWorkouts().size() + " workouts");
        }

        Statistics stats = entityManager.unwrap(Session.class)
                .getSessionFactory()
                .getStatistics();

        assertThat(stats.getQueryExecutionCount())
                .isNotEqualTo(1 + users.size());

        System.out.println("Total queries executed: " + stats.getQueryExecutionCount());
    }

    @Test
    public void whenAccessWorkoutTemplatesAfterFindAll_thenTriggersNPlusOne() {
        Session session = entityManager.unwrap(Session.class);
        session.getSessionFactory().getStatistics().clear();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            System.out.println("User " + user.getId() + " has " + user.getWorkoutTemplates().size() + " workout templates");
        }

        Statistics stats = entityManager.unwrap(Session.class)
                .getSessionFactory()
                .getStatistics();

        assertThat(stats.getQueryExecutionCount())
                .isNotEqualTo(1 + users.size());

        System.out.println("Total queries executed for workout templates: " + stats.getQueryExecutionCount());
    }

    @Test
    public void whenAccessBothCollections_thenTriggers2NPlusOne() {
        Session session = entityManager.unwrap(Session.class);
        session.getSessionFactory().getStatistics().clear();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            System.out.println("User " + user.getId() + " has " +
                    user.getWorkouts().size() + " workouts and " +
                    user.getWorkoutTemplates().size() + " workout templates");
        }

        Statistics stats = entityManager.unwrap(Session.class)
                .getSessionFactory()
                .getStatistics();

        assertThat(stats.getQueryExecutionCount())
                .isNotEqualTo(1 + 2 * users.size());

        System.out.println("Total queries executed for both collections: " + stats.getQueryExecutionCount());
    }
}