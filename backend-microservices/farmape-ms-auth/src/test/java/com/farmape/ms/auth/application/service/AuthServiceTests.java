package com.farmape.ms.auth.application.service;

import com.farmape.ms.auth.Application;
import com.farmape.ms.auth.auth.service.AuthService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = Application.class,
        properties = {
                "spring.config.import=optional:configserver:",
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.driver-class-name=org.h2.Driver",
                "spring.datasource.username=sa",
                "spring.datasource.password=",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop",
                "app.jwt.secret=clave-test-minimo-32-caracteres-para-hmac256",
                "app.jwt.expiration-minutes=60",
                "app.jwt.refresh-expiration-days=7",
                "app.bootstrap.admin.enabled=false",
                "eureka.client.enabled=false",
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false"
        }
)
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Test
    void authServiceCargaCorrectamente() {
        assertThat(authService).isNotNull();
    }
}