package org.example.venn;

import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class VennApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void loadsApplicationContextWithFlywayAndH2() {
        // Given
        final var context = applicationContext;

        // When
        final var entityManagerFactory = context.getBean(EntityManagerFactory.class);

        // Then
        assertThat(entityManagerFactory.isOpen()).isTrue();
    }
}
