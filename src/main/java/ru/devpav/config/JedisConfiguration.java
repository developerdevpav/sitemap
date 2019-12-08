package ru.devpav.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("application.yaml")
public class JedisConfiguration {

}
