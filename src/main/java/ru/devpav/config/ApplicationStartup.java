package ru.devpav.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.devpav.sitemap.SitemapService;

@Component
public class ApplicationStartup implements ApplicationRunner {


    private final SitemapService sitemapService;

    public ApplicationStartup(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }



    @Override
    public void run(ApplicationArguments args) {

    }

}
