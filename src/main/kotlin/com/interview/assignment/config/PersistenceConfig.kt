package com.interview.assignment.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.domain.AuditorAware
import java.time.ZonedDateTime
import java.util.Optional.of

@Configuration
class PersistenceConfig {

    @Bean
    fun auditingDateTimeProvider() = DateTimeProvider { of(ZonedDateTime.now()) }

    @Bean
    fun springSecurityAuditorAware(): AuditorAware<String> = AuditorAware<String> { of("system") }

}