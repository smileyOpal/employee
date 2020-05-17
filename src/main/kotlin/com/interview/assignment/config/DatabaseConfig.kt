package com.interview.assignment.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import java.time.ZonedDateTime
import java.util.*

@Configuration
@EnableJpaRepositories("com.interview.assignment.repository")
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware", dateTimeProviderRef = "auditingDateTimeProvider")
class DatabaseConfig {

    /**
     * Defined a customize auditingDateTimeProvider for JPA Auditor
     */
    @Bean
    fun auditingDateTimeProvider() = DateTimeProvider { Optional.of(ZonedDateTime.now()) }

    /**
     * Declare springSecurityAuditorAware for JPA Auditor implementor to look up current principal
     */
    @Bean
    fun springSecurityAuditorAware(): AuditorAware<String> = AuditorAware<String> { Optional.of("system") }
}