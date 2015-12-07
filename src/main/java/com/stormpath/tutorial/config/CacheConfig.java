package com.stormpath.tutorial.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.hazelcast.web.SessionListener;
import com.hazelcast.web.spring.SpringAwareWebFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;

import javax.servlet.DispatcherType;

@Configuration
public class CacheConfig {
    @Bean
    public FilterRegistrationBean hazelcastFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new SpringAwareWebFilter());

        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.addUrlPatterns("/*");
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
        registration.addInitParameter("sticky-session", "false");

        return registration;
    }

    @Bean
    public ServletListenerRegistrationBean<SessionListener> hazelcastSessionListener() {
        return new ServletListenerRegistrationBean<SessionListener>(new SessionListener());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

    @Bean
    public CacheManager cacheManager() {
        // The Stormpath SDK knows to use the Spring CacheManager automatically
        return new HazelcastCacheManager(hazelcastInstance());
    }
}