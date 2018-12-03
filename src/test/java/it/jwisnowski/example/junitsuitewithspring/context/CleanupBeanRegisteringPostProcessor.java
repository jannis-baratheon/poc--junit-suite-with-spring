package it.jwisnowski.example.junitsuitewithspring.context;

import java.io.Closeable;
import java.util.Arrays;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

class CleanupBeanRegisteringPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private static final String CLEANUPER_BEAN_NAME = "cleanuper";

    private final Iterable<AutoCloseable> autoCloseablesToClose;

    CleanupBeanRegisteringPostProcessor(AutoCloseable... autoCloseablesToClose) {
        this.autoCloseablesToClose = Arrays.asList(autoCloseablesToClose);
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        beanDefinitionRegistry.registerBeanDefinition(
            CLEANUPER_BEAN_NAME, getCleanuperBeanDefinition());
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }

    private BeanDefinition getCleanuperBeanDefinition() {
        GenericBeanDefinition cleanuperBeanDefinition =
            new GenericBeanDefinition();

        cleanuperBeanDefinition.setBeanClass(Cleanuper.class);
        cleanuperBeanDefinition.setConstructorArgumentValues(
            getConstructorArgumentValues());
        cleanuperBeanDefinition.setAutowireCandidate(false);

        return cleanuperBeanDefinition;
    }

    private ConstructorArgumentValues getConstructorArgumentValues() {
        ConstructorArgumentValues constructorArgumentValues =
            new ConstructorArgumentValues();
        constructorArgumentValues.addGenericArgumentValue(
            autoCloseablesToClose);

        return constructorArgumentValues;
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
