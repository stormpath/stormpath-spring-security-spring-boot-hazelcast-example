#Stormpath is Joining Okta
We are incredibly excited to announce that [Stormpath is joining forces with Okta](https://stormpath.com/blog/stormpaths-new-path?utm_source=github&utm_medium=readme&utm-campaign=okta-announcement). Please visit [the Migration FAQs](https://stormpath.com/oktaplusstormpath?utm_source=github&utm_medium=readme&utm-campaign=okta-announcement) for a detailed look at what this means for Stormpath users.

We're available to answer all questions at [support@stormpath.com](mailto:support@stormpath.com).


## Spring Boot + Spring Security + Hazelcast

This example is based on the Spring Boot WebMVC + Spring Security Tutorial found in the 
[stormpath-sdk-java](https://github.com/stormpath/stormpath-sdk-java/tree/master/tutorials/spring-boot/03-spring-security-refined) 
project.

You can follow the instructions at the beginning of the [Tutorial Docs](http://docs.stormpath.com/java/spring-boot-web/tutorial.html#get-an-api-key) 
to get started with Stormpath.

Then, jump right in to the tutorial example this project is based on [here](http://docs.stormpath.com/java/spring-boot-web/tutorial.html#spring-security-refined).

This example Builds on the tutorial by adding in [Hazelcast](https://hazelcast.com/) cacheing support as well as a custom CSRF token manager that delegates to 
Spring Security's cache manager, which in turn delegates to Hazelcast.

The relevant file additions are:

* [CacheConfig](https://github.com/stormpath/stormpath-spring-boot-hazelcast-example/blob/master/src/main/java/com/stormpath/tutorial/config/CacheConfig.java)
* [DefaultCookieConfig](https://github.com/stormpath/stormpath-spring-boot-hazelcast-example/blob/master/src/main/java/com/stormpath/tutorial/config/DefaultCookieConfig.java)
* [CacheCsrfTokenRepository](https://github.com/stormpath/stormpath-spring-boot-hazelcast-example/blob/master/src/main/java/com/stormpath/tutorial/config/CacheCsrfTokenRepository.java)