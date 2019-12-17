package ru.devpav.repository;

import org.springframework.data.repository.NoRepositoryBean;
import ru.devpav.domain.BaseResource;

@NoRepositoryBean
public interface BaseResourceRepository<T extends BaseResource> {
}
