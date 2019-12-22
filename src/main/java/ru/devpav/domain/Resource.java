package ru.devpav.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tbx_resource")
public class Resource extends BaseResource {

    @OneToMany(mappedBy = "resource",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private Set<Link> links;

    @Column(name = "time")
    private Long time;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        if (!super.equals(o)) return false;
        Resource resource = (Resource) o;
        return Objects.equals(getTime(), resource.getTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTime());
    }

    @Override
    public String toString() {
        return "Resource{" +
                "links=" + links +
                ", time=" + time +
                '}';
    }
}
