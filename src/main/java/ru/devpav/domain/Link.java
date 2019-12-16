package ru.devpav.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Link extends BaseResource {

    private Long time;
    private String link;
    private Boolean middling = false;
    private Boolean sitemap = false;
    private Integer hash;
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
                Objects.equals(getHash(), link1.getHash()) &&
                Objects.equals(getParent(), link1.getParent());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTime(), getLink(), getMiddling(), getSitemap(), getHash(), getParent());
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
