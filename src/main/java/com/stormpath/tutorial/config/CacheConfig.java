package com.stormpath.tutorial.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return Hazelcast.newHazelcastInstance();
    }

    @Bean
    public CacheManager cacheManager() {
        // The Stormpath SDK knows to use the Spring CacheManager automatically
        return new HazelcastCacheManager(hazelcastInstance());
    }

    /*

    Uncomment the next 3 bean definitions if you want to use Spring Security's default HTTP Session-based CSRF
    protection.  This clusters sessions in Hazelcast and ensures that CSRF will work across http nodes if/when
    sticky sessions are disabled:


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
        return new ServletListenerRegistrationBean<>(new SessionListener());
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }
    */
}