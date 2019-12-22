package ru.devpav.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "tbx_link")
public class Link extends BaseResource {

    @Column(name = "time")
    private Long time;

    @Column(name = "link")
    private String link;

    @Column(name = "is_middling")
    private Boolean middling = false;

    @Column(name = "is_sitemap")
    private Boolean sitemap = false;

    @Column(name = "hash")
    private Integer hash;

    @ManyToOne(cascade = CascadeType.ALL)
    private Link parent;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;
        if (!super.equals(o)) return false;
        Link link1 = (Link) o;
        return Objects.equals(getTime(), link1.getTime()) &&
                Objects.equals(getLink(), link1.getLink()) &&
                Objects.equals(getMiddling(), link1.getMiddling()) &&
                Objects.equals(getSitemap(), link1.getSitemap()) &&
                Objects.equals(getHash(), link1.getHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTime(), getLink(), getMiddling(), getSitemap(), getHash());
    }


    @Override
    public String toString() {
        return "Link{" +
                "time=" + time +
                ", link='" + link + '\'' +
                ", middling=" + middling +
                ", sitemap=" + sitemap +
                ", hash=" + hash +
                ", parent=" + parent +
                '}';
    }
}
