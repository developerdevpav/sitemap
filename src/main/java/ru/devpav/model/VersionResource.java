package ru.devpav.model;

import java.util.Date;
import java.util.Objects;

public class VersionResource implements Comparable<VersionResource> {

    private Resource resource;
    private Date date = new Date();

    public VersionResource(Resource resource) {
        this.resource = resource;
    }

    @Override
    public int compareTo(VersionResource o) {
        return o.date.compareTo(this.date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionResource)) return false;
        VersionResource that = (VersionResource) o;
        return Objects.equals(resource, that.resource) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resource, date);
    }

    @Override
    public String toString() {
        return "VersionResource{" +
                "resource=" + resource +
                ", date=" + date +
                '}';
    }
}
