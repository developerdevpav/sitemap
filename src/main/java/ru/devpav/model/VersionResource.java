package ru.devpav.model;

import java.util.Objects;

public class VersionResource implements Comparable<VersionResource> {

    private Resource resource;
    private Long time;

    public VersionResource(Resource resource, Long time) {
        this.resource = resource;
        this.time = time;
    }


    public Resource getResource() {
        return resource;
    }

    public Long getTime() {
        return time;
    }

    @Override
    public int compareTo(VersionResource o) {
        return this.time.compareTo(o.time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionResource)) return false;
        VersionResource that = (VersionResource) o;
        return Objects.equals(resource, that.resource) &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, time);
    }

    @Override
    public String toString() {
        return "VersionResource{" +
                "resource=" + resource +
                ", date=" + time +
                '}';
    }

}
