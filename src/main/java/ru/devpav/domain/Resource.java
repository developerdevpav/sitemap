package ru.devpav.domain;

import java.util.Objects;
import java.util.Set;

public class Resource extends BaseResource {

    private String link;
    private Set<Link> links;
    private Long time;


    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }


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
