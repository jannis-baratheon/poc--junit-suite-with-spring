package it.jwisnowski.example.junitsuitewithspring;

import static java.util.Collections.singletonList;

import it.jwisnowski.example.junitsuitewithspring.dockers.ADockerContainer;
import java.io.Closeable;
import org.junit.ClassRule;
import org.junit.Rule;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

@ContextConfiguration(
    initializers = AbstractTestClass.ContextInitializer.class,
    classes = AbstractTestClass.Conf.class)
public class AbstractTestClass {

    @ClassRule
    public final static SpringClassRule springClassRule = new SpringClassRule();

    @Rule
    public final SpringMethodRule springMethodRule = new SpringMethodRule();

    public static class ContextInitializer
        implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            ADockerContainer aDockerContainer = new ADockerContainer();
            aDockerContainer.start();

            context.getBeanFactory().registerResolvableDependency(
                ADockerContainer.class, aDockerContainer);
            context.addBeanFactoryPostProcessor(new BeanDefinitionRegistryPostProcessor() {
                @Override
                public void postProcessBeanDefinitionRegistry(
                    BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
                    GenericBeanDefinition cleanuperBeanDefinition = new GenericBeanDefinition();
                    cleanuperBeanDefinition.setBeanClass(Cleanuper.class);
                    ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
                    constructorArgumentValues.addGenericArgumentValue(
                        singletonList((AutoCloseable) aDockerContainer::stop));
                    cleanuperBeanDefinition.setConstructorArgumentValues(constructorArgumentValues);
                    cleanuperBeanDefinition.setAutowireCandidate(false);
                    beanDefinitionRegistry.registerBeanDefinition(
                        "cleanuper",
                        cleanuperBeanDefinition);
                }

                @Override
                public void postProcessBeanFactory(
                    ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
                }
            });
        }

        private static class Cleanuper implements Closeable {
            private final Iterable<AutoCloseable> autoCloseables;

            public Cleanuper(Iterable<AutoCloseable> autoCloseables) {
                this.autoCloseables = autoCloseables;
            }

            @Override
            public void close() {
                for (AutoCloseable autoCloseable : autoCloseables) {
                    try {
                        autoCloseable.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Configuration
    public static class Conf {
        @Bean
        public Object beanCausingContextInitializationException() {
            throw new RuntimeException();
        }
    }

}
