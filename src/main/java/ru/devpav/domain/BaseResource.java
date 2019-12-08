package ru.devpav.domain;

import java.util.Objects;

public class BaseResource extends JetisBasic {

    private String link;


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
        if (!super.equals(o)) return false;
        BaseResource that = (BaseResource) o;
        return Objects.equals(getLink(), that.getLink());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLink());
    }


    @Override
    public String toString() {
        return "BaseResource{" +
                "link='" + link + '\'' +
                '}';
    }
}
