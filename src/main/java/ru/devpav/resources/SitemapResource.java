package ru.devpav.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.devpav.domain.Resource;
import ru.devpav.sitemap.SitemapService;

import java.util.Set;

@RestController
@RequestMapping("/api/sitemap")
public class SitemapResource {

    private final SitemapService sitemapService;

    @Autowired
    public SitemapResource(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }


    @GetMapping
    public ResponseEntity<Resource> getLinks(@RequestParam("resource") String resource) {
        return ResponseEntity.ok(sitemapService.getLinks(resource));
    }

    @GetMapping("/analytic")
    public ResponseEntity<SitemapService.AnalyticResource> getAnalytic(@RequestParam("resource") String resource) {
        return ResponseEntity.ok(sitemapService.getAnalyticResource(resource));
    }

    @GetMapping("/links")
    public ResponseEntity<Set<String>> getStringLink(@RequestParam("resource") String resource) {
        return ResponseEntity.ok(sitemapService.getStringLink(resource));
    }

}
