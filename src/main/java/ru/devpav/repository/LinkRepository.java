package ru.devpav.repository;

import org.springframework.stereotype.Repository;
import ru.devpav.domain.Link;

@Repository
public interface LinkRepository extends BaseResourceRepository<Link> {

    boolean existsByLinkAndHashAndMiddling(String link, Integer hash, Boolean middling);

}
