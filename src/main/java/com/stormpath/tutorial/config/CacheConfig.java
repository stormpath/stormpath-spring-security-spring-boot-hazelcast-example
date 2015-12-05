package com.stormpath.tutorial.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.hazelcast.web.SessionListener;
import com.hazelcast.web.spring.SpringAwareWebFilter;
import com.stormpath.sdk.api.ApiKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.DispatcherType;

@Configuration
public class CacheConfig {

    @Autowired
    @Qualifier("stormpathClientApiKey")
    private ApiKey stormpathClientApiKey;

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

    @Bean
    public CacheManager cacheManager() {
        // The Stormpath SDK knows to use the Spring CacheManager automatically
        return new HazelcastCacheManager(hazelcastInstance());
    }

    @Bean
    public CsrfTokenRepository stormpathCsrfTokenRepository() {
        long ttlMillis = 60 * 60 * 1000; //millis in one hour
        Cache cache = cacheManager().getCache("csrfTokenNonces");
        return new CacheCsrfTokenRepository(cache, stormpathClientApiKey.getSecret(), ttlMillis);
    }
}