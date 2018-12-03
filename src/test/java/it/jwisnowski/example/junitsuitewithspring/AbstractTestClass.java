package it.jwisnowski.example.junitsuitewithspring;

import it.jwisnowski.example.junitsuitewithspring.context.DockersContextInitializer;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@ContextConfiguration(
    initializers = DockersContextInitializer.class,
    classes = AbstractTestClass.Conf.class)
public class AbstractTestClass {

    @ClassRule
    public final static SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    @Configuration
    public static class Conf {
        @Bean
        public Object beanCausingContextInitializationException() {
            throw new RuntimeException();
        }
    }
}
