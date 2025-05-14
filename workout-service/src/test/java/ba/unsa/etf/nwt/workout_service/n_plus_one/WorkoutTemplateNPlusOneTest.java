package ba.unsa.etf.nwt.workout_service.n_plus_one;

import ba.unsa.etf.nwt.workout_service.HibernateStatisticsLogger;
import ba.unsa.etf.nwt.workout_service.domain.ExerciseTemplate;
import ba.unsa.etf.nwt.workout_service.domain.User;
import ba.unsa.etf.nwt.workout_service.domain.WorkoutTemplate;
import ba.unsa.etf.nwt.workout_service.repositories.ExerciseTemplateRepository;
import ba.unsa.etf.nwt.workout_service.repositories.UserRepository;
import ba.unsa.etf.nwt.workout_service.repositories.WorkoutTemplateRepository;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class WorkoutTemplateNPlusOneTest {
    @Autowired
    private WorkoutTemplateRepository workoutTemplateRepository;

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
    public void whenAccessExerciseTemplatesAfterFindAll_thenTriggersNPlusOne() {
        statisticsLogger.logStatistics();

        List<WorkoutTemplate> templates = workoutTemplateRepository.findAll();

        statisticsLogger.logStatistics();

        for (WorkoutTemplate template : templates) {
            System.out.println("Template " + template.getName() + " has " +
                    template.getExerciseTemplates().size() + " exercise templates");
        }

        Statistics stats = entityManager.unwrap(Session.class)
                .getSessionFactory()
                .getStatistics();

        assertThat(stats.getQueryExecutionCount())
                .isNotEqualTo(1 + templates.size());

        System.out.println("Total queries executed: " + stats.getQueryExecutionCount());
    }

}