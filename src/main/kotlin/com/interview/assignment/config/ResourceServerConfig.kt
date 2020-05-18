package com.interview.assignment.config

import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer


@Configuration
@EnableResourceServer
class ResourceServerConfig : ResourceServerConfigurerAdapter() {

    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId("resource-server-rest-api")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
                .antMatcher("/api/**")
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/**").hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/**").hasAnyRole("ADMIN")
                .antMatchers("/api/**").hasAnyRole("USER", "ADMIN")
    }
}