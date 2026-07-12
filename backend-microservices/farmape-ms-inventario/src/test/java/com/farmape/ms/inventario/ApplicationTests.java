package com.farmape.ms.inventario;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.config.import=optional:configserver:",
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false",
        "spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration"
})
class ApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void hasSpringBootBanner() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/banner.txt")) {
            assertThat(input).isNotNull();

            String banner = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(banner)
                    .hasSizeGreaterThan(100)
                    .contains("${application.title}")
                    .contains("${application.version}")
                    .contains("Powered by Spring Boot")
                    .contains("${spring-boot.version}");
        }
    }
}
