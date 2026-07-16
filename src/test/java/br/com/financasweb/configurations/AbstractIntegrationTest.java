package br.com.financasweb.configurations;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractIntegrationTest {

    protected static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16")
                    .withDatabaseName("bd-api-financas-test")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static {
        // If CI provides a datasource via environment variables (for example
        // GitHub Actions service container) we don't need to start Testcontainers
        // locally. Start the postgres Testcontainer only when no external
        // datasource is provided.
        String ciUrl = System.getenv("SPRING_DATASOURCE_URL");
        String ciUser = System.getenv("SPRING_DATASOURCE_USERNAME");
        String ciPass = System.getenv("SPRING_DATASOURCE_PASSWORD");

        if (ciUrl == null || ciUser == null || ciPass == null) {
            // start Testcontainers only for local/dev runs
            postgres.start();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // If CI provides datasource configuration via environment variables,
        // prefer those values (this allows GitHub Actions to supply a service
        // Postgres container). Otherwise fall back to Testcontainers values.
        String ciUrl = System.getenv("SPRING_DATASOURCE_URL");
        String ciUser = System.getenv("SPRING_DATASOURCE_USERNAME");
        String ciPass = System.getenv("SPRING_DATASOURCE_PASSWORD");

        if (ciUrl != null && ciUser != null && ciPass != null) {
            registry.add("spring.datasource.url", () -> ciUrl);
            registry.add("spring.datasource.username", () -> ciUser);
            registry.add("spring.datasource.password", () -> ciPass);
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        } else {
            registry.add("spring.datasource.url", postgres::getJdbcUrl);
            registry.add("spring.datasource.username", postgres::getUsername);
            registry.add("spring.datasource.password", postgres::getPassword);
            registry.add("spring.datasource.driver-class-name", postgres::getDriverClassName);
        }
    }

}
