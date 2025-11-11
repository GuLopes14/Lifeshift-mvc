package br.com.lifeshift.lifeshift.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfiguration {
    // Spring Boot autoconfigura o Simple Cache por padrão
    // Para produção, considere usar Redis ou Caffeine
}
