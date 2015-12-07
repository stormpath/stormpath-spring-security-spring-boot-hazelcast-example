/*
 * Copyright 2015 Stormpath, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stormpath.tutorial.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import static com.stormpath.spring.config.StormpathWebSecurityConfigurer.stormpath;

/**
 * @since 1.0.RC6
 */
@Configuration
public class SpringSecurityWebAppConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .apply(stormpath()).and()
                .authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/assets/**").permitAll();
    }

    /*
     Uncomment the following lines if you would like digitally signed CSRF tokens to be stored in Hazelcast instead of
     Spring's default approach of using the Http Session (which requires session clustering in a distributed app).

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    @Qualifier("stormpathClientApiKey")
    private ApiKey stormpathClientApiKey;

    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        long ttlMillis = 60 * 60 * 1000; //millis in one hour
        Cache cache = cacheManager.getCache("csrfTokenNonces");
        return new CacheCsrfTokenRepository(cache, stormpathClientApiKey.getSecret(), ttlMillis);
    }
    */
}