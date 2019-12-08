package ru.devpav.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.devpav.domain.Resource;
import ru.devpav.repository.ResourceRepository;

@Component
public class ApplicationStartup implements ApplicationRunner {

    private final ResourceRepository resourceRepository;

    public ApplicationStartup(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        final Resource resource = new Resource();
        resource.setLink("https://value.ru");
    }

}
