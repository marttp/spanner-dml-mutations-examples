package au.com.helixta.spannerexamples;

import com.google.cloud.spanner.DatabaseId;
import com.google.cloud.spanner.SpannerOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.data.spanner.core.admin.DatabaseIdProvider;
import org.springframework.cloud.gcp.data.spanner.repository.config.EnableSpannerRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLException;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestSpanner.AppConfiguration.class)
class TestSpanner
{
    private static final Logger log = LoggerFactory.getLogger(TestSpanner.class);

    private static final DatabaseId DATABASE_ID = DatabaseId.of("helix-sydney", "prunge-test", "cats");
    private static final String JDBC_URI = "jdbc:cloudspanner:/" + DATABASE_ID.getName();
    private static final SpannerSql spannerSql = new SpannerSql(JDBC_URI);

    private static final SpannerMutations spannerMutations = new SpannerMutations(SpannerOptions.getDefaultInstance().getService(),
                                                                                  DATABASE_ID);
    @Autowired
    private SpannerSpringData spannerSpringData;

    @BeforeEach
    void setUpDatabase()
    throws SQLException
    {
        spannerSql.setUpTables();
    }

    @Test
    void testSql()
    throws SQLException
    {
        log.info("JDBC test:");
        spannerSql.run();
    }

    @Test
    void testMutations()
    {
        log.info("Mutations test:");
        spannerMutations.run();
    }

    @Test
    void testSpringData()
    {
        log.info("Spring Data test:");
        spannerSpringData.run();
    }

    @SpringBootApplication
    @EnableSpannerRepositories
    public static class AppConfiguration
    {
        @Bean
        public DatabaseIdProvider databaseIdProvider()
        {
            return () -> DATABASE_ID;
        }
    }
}
