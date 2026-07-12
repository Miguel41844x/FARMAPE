package com.farmape.ms.eureka;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.config.import=optional:configserver:",
        "eureka.client.register-with-eureka=false",
        "eureka.client.fetch-registry=false"
})
class ApplicationTests {

    @Test
    void contextLoads() {
    }
}
