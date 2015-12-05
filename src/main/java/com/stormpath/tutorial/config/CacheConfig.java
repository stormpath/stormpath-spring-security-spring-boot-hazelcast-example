package com.stormpath.tutorial.config;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.stormpath.sdk.api.ApiKey;
import com.stormpath.sdk.servlet.csrf.CsrfTokenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @Bean
    public CsrfTokenManager stormpathCsrfTokenManager() {

        final CsrfTokenRepository repo = stormpathCsrfTokenRepository();

        return new CsrfTokenManager() {
            @Override
            public String getTokenName() {
                return "csrfToken";
            }

            @Override
            public String createCsrfToken(HttpServletRequest request, HttpServletResponse response) {
                CsrfToken csrfToken = repo.loadToken(request);
                if (csrfToken == null) {
                    csrfToken = repo.generateToken(request);
                    repo.saveToken(csrfToken, request, response);
                }
                return csrfToken.getToken();
            }

            @Override
            public boolean isValidCsrfToken(HttpServletRequest request, HttpServletResponse response, String csrfToken) {
                CsrfToken loadedCSRFToken = repo.loadToken(request);
                return csrfToken != null  && loadedCSRFToken != null && csrfToken.equals(loadedCSRFToken.getToken());
            }
        };
    }
}