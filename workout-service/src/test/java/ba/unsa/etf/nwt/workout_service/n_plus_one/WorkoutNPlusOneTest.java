package ba.unsa.etf.nwt.workout_service.n_plus_one;

import ba.unsa.etf.nwt.workout_service.HibernateStatisticsLogger;
import ba.unsa.etf.nwt.workout_service.domain.Workout;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutRepository;
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
public class WorkoutNPlusOneTest {
    @Autowired
    private WorkoutRepository workoutRepository;

    @Autowired
    private HibernateStatisticsLogger statisticsLogger;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    public void setup() {
        // Clear previous statistics
        Session session = entityManager.unwrap(Session.class);
        session.getSessionFactory().getStatistics().clear();

        // Flush and clear to ensure all entities are persisted
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    public void whenAccessExercisesAfterFindAll_thenTriggersNPlusOne() {
        statisticsLogger.logStatistics();

        List<Workout> workouts = workoutRepository.findAll();

        statisticsLogger.logStatistics();

        for (Workout workout : workouts) {
            workout.getExercises().size();
        }

        Statistics stats = entityManager.unwrap(Session.class)
                .getSessionFactory()
                .getStatistics();

        assertThat(stats.getQueryExecutionCount())
                .isNotEqualTo(1 + workouts.size());
        assertThat(stats.getQueryExecutionCount())
                .isEqualTo(1);

        System.out.println("Total queries executed: " + stats.getQueryExecutionCount());
    }
}