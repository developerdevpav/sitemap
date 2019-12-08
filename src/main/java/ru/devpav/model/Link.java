package ru.devpav.model;

import java.util.Objects;

public class Link {

    private String date;
    private String link;
    private Boolean middling;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Boolean getMiddling() {
        return middling;
    }

    public void setMiddling(Boolean middling) {
        this.middling = middling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Link)) return false;
        Link link1 = (Link) o;
        return Objects.equals(getDate(), link1.getDate()) &&
                Objects.equals(getLink(), link1.getLink()) &&
                Objects.equals(getMiddling(), link1.getMiddling());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), getLink(), getMiddling());
    }

    @Override
    public String toString() {
        return "Link{" +
                "date='" + date + '\'' +
                ", link='" + link + '\'' +
                ", middling=" + middling +
                '}';
    }
}
