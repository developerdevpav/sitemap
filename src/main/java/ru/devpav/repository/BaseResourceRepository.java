package ru.devpav.repository;

import ru.devpav.domain.BaseResource;

public interface BaseResourceRepository<T extends BaseResource> extends JetisBasicRepository<T> {
}
