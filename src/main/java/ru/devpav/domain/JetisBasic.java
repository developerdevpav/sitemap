package ru.devpav.domain;

import java.util.Objects;

public class JetisBasic {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JetisBasic)) return false;
        JetisBasic that = (JetisBasic) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "JetisBasic{" +
                "id='" + id + '\'' +
                '}';
    }
}
