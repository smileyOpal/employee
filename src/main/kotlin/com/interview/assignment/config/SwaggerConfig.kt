package com.interview.assignment.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
class SwaggerConfig {

    @Bean
    fun api(): Docket = Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.interview.assignment.web.rest"))
            .paths(PathSelectors.ant("/api/**"))
            .build()
            .apiInfo(apiInfo())

    private fun apiInfo() = ApiInfo(
            "Employee REST API",
            "Interview assignment",
            "0.0.1",
            "Terms of service",
            null,
            "License of API",
            "API license URL",
            emptyList())
}