package it.jwisnowski.example.junitsuitewithspring.context;

import com.google.common.collect.ImmutableMap;
import it.jwisnowski.example.junitsuitewithspring.dockers.ADockerContainer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MapPropertySource;

public class DockersContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext context) {
        ADockerContainer aDockerContainer = new ADockerContainer();
        aDockerContainer.start();

        setProperties(context, aDockerContainer);

        context.getBeanFactory().registerResolvableDependency(
            ADockerContainer.class, aDockerContainer);

        context.addBeanFactoryPostProcessor(
            new CleanupBeanRegisteringPostProcessor(aDockerContainer::stop));
    }

    private static void setProperties(ConfigurableApplicationContext context,
                                      ADockerContainer container) {
        ImmutableMap<String, Object> dockerProperties = ImmutableMap.of(
            "dockerizedService.host", container.getHost(),
            "dockerizedService.port", container.getPort());

        context.getEnvironment()
            .getPropertySources()
            .addFirst(new MapPropertySource("dockers", dockerProperties));
    }
}
