package ru.devpav.domain;

import java.util.Objects;

public class BaseResource {

    private Long id;
    private String link;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseResource)) return false;
        BaseResource that = (BaseResource) o;
        return Objects.equals(getId(), that.getId()) &&
                Objects.equals(getLink(), that.getLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLink());
    }

}
