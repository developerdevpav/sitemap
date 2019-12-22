package ru.devpav.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.devpav.domain.BaseResource;

@NoRepositoryBean
public interface BaseResourceRepository<T extends BaseResource> extends JpaRepository<T, Long> {

    T findByLink(String link);

}
