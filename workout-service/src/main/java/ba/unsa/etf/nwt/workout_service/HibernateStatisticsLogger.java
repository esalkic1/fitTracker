package ba.unsa.etf.nwt.workout_service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.stat.Statistics;
import org.springframework.stereotype.Component;

@Component
public class HibernateStatisticsLogger {

    @PersistenceContext
    private EntityManager entityManager;

    public void logStatistics() {
        Session session = entityManager.unwrap(Session.class);
        Statistics stats = session.getSessionFactory().getStatistics();

        System.out.println("\n=== Hibernate Statistics ===");
        System.out.println("Queries executed: " + stats.getQueryExecutionCount());
        System.out.println("Entity fetches: " + stats.getEntityFetchCount());
        System.out.println("==========================\n");
    }
}
