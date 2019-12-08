package ru.devpav.model;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class Resource {

    private String link;
    private Set<Link> links;
    private Set<String> sitemaps;
    private Date date;


    public Set<String> getSitemaps() {
        return sitemaps;
    }

    public void setSitemaps(Set<String> sitemaps) {
        this.sitemaps = sitemaps;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource resource = (Resource) o;
        return Objects.equals(getLink(), resource.getLink()) &&
                Objects.equals(getLinks(), resource.getLinks()) &&
                Objects.equals(getSitemaps(), resource.getSitemaps()) &&
                Objects.equals(getDate(), resource.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLink(), getLinks(), getSitemaps(), getDate());
    }


    @Override
    public String toString() {
        return "Resource{" +
                "link='" + link + '\'' +
                ", date=" + date +
                '}';
    }
}
