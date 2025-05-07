package ba.unsa.etf.nwt.nutrition_service.services;

import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsService.class);
    private final Statistics statistics;

    public StatisticsService(Statistics statistics) {
        this.statistics = statistics;
    }

    public void clear() {
        statistics.clear();
    }

    public void logStatistics() {
        logger.info("======= HIBERNATE STATISTICS =======");
        logger.info("Queries executed: {}", statistics.getQueryExecutionCount());
        logger.info("Entity loads: {}", statistics.getEntityLoadCount());
        logger.info("Collections loaded: {}", statistics.getCollectionLoadCount());
        logger.info("Second-level cache hits: {}", statistics.getSecondLevelCacheHitCount());
        logger.info("Second-level cache misses: {}", statistics.getSecondLevelCacheMissCount());
        logger.info("===================================");
    }

    public long getQueryCount() {
        return statistics.getQueryExecutionCount();
    }

    public long getEntityLoadCount() {
        return statistics.getEntityLoadCount();
    }

    public long getCollectionLoadCount() {
        return statistics.getCollectionLoadCount();
    }

    public void printQueries() {
        logger.info("======= SQL QUERIES EXECUTED =======");
        for (String query : statistics.getQueries()) {
            logger.info(query);
        }
        logger.info("===================================");
    }
}