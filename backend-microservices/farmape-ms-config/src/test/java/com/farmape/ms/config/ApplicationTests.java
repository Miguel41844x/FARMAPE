package com.farmape.ms.config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private EnvironmentRepository environmentRepository;

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

    @Test
    void publishesEurekaConfiguration() {
        Environment environment = environmentRepository.findOne("farmape-ms-eureka", "default", null);

        assertThat(environment.getPropertySources())
                .extracting(PropertySource::getSource)
                .anySatisfy(source -> {
                    assertThat(source.get("spring.application.name")).isEqualTo("farmape-ms-eureka");
                    assertThat(source.get("server.port")).isEqualTo(8761);
                });
        assertThat(environment.toString())
                .contains("farmape-ms-eureka")
                .contains("eureka");
    }

    @Test
    void publishesGatewayConfiguration() {
        Environment environment = environmentRepository.findOne("farmape-ms-gateway", "default", null);

        assertThat(environment.getPropertySources())
                .extracting(PropertySource::getSource)
                .anySatisfy(source -> {
                    assertThat(source.get("spring.application.name")).isEqualTo("farmape-ms-gateway");
                    assertThat(source.get("server.port")).isEqualTo(8080);
                });
        assertThat(environment.toString())
                .contains("farmape-ms-gateway");
    }
}
