package ru.devpav.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;

@Getter
@Setter
public class Resource extends BaseResource {

    private String link;
    private Set<Link> links;
    private Long time;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        if (!super.equals(o)) return false;
        Resource resource = (Resource) o;
        return Objects.equals(getLink(), resource.getLink()) &&
                Objects.equals(getLinks(), resource.getLinks()) &&
                Objects.equals(getTime(), resource.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLink(), getLinks(), getTime());
    }

    @Override
    public String toString() {
        return "Resource{" +
                "link='" + link + '\'' +
                ", links=" + links +
                ", time=" + time +
                '}';
    }
}
