package com.pedrolg;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class TesterContainerTest {

    @Container
    private static final PostgreSQLContainer<?> PostgreSQLContainer = new PostgreSQLContainer<>("postgres:latest").withDatabaseName("amigoscode-unit-test").withUsername("amigoscode").withPassword("password");

    @DynamicPropertySource
    private static void registerDataSourcePropety(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", PostgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", PostgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", PostgreSQLContainer::getPassword);
    }

    @Test
    void canAppyMigrationsWithFlyway() {
        Flyway flyway = Flyway.configure().dataSource(PostgreSQLContainer.getJdbcUrl(), PostgreSQLContainer.getUsername(), PostgreSQLContainer.getPassword()).load();
        flyway.migrate();
    }
}
