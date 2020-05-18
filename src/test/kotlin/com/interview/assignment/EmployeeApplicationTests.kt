package com.interview.assignment

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.interview.assignment.service.dto.RequestCreateEmployeeDTO
import com.interview.assignment.service.dto.RequestUpdateEmployeeDTO
import com.interview.assignment.service.dto.ResponseEmployeeDTO
import com.interview.assignment.web.rest.EmployeeResource
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.notNullValue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerSecurityConfiguration
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.ZonedDateTime

@RunWith(SpringRunner::class)
@SpringBootTest
@ComponentScan(excludeFilters = [
    ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            value = [ResourceServerConfiguration::class, AuthorizationServerEndpointsConfiguration::class, AuthorizationServerSecurityConfiguration::class])
])
class EmployeeApplicationTests {

    private lateinit var restUserMockMvc: MockMvc

    @Autowired
    private lateinit var employeeResource: EmployeeResource

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    private lateinit var objectMapper: ObjectMapper

    @Before
    fun setup() {
        objectMapper = ObjectMapper()
        objectMapper.registerModule(Jdk8Module())
        objectMapper.registerModule(JavaTimeModule())

        restUserMockMvc = MockMvcBuilders.standaloneSetup(employeeResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setMessageConverters(jacksonMessageConverter)
                .build()
    }

    @Test
    fun `Assert create new employee and delete newly create employee`() {

        val request = RequestCreateEmployeeDTO("integration", "test")

        val createResult = restUserMockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.firstname").value(notNullValue()))
                .andExpect(jsonPath("$.lastname").value(notNullValue()))
                .andExpect(jsonPath("$.joinDate").value(notNullValue()))
                .andExpect(jsonPath("$.createdBy").value(notNullValue()))
                .andExpect(jsonPath("$.createdDate").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedBy").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate").value(notNullValue()))
                .andReturn()

        val createdEndpoint = createResult.response.getHeader("Location").toString()

        restUserMockMvc.perform(delete(createdEndpoint)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
    }

    @Test
    fun `Assert create new employee with empty value`() {

        val request = RequestCreateEmployeeDTO("", "")

        restUserMockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `Assert create new employee with null value`() {

        val request = RequestCreateEmployeeDTO(null, null)

        restUserMockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `Assert update employee`() {

        val request = RequestCreateEmployeeDTO("integration", "test")

        val createResult = restUserMockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.firstname").value(notNullValue()))
                .andExpect(jsonPath("$.lastname").value(notNullValue()))
                .andExpect(jsonPath("$.joinDate").value(notNullValue()))
                .andExpect(jsonPath("$.createdBy").value(notNullValue()))
                .andExpect(jsonPath("$.createdDate").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedBy").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate").value(notNullValue()))
                .andReturn()

        val entity = objectMapper.readValue(createResult.response.contentAsString, ResponseEmployeeDTO::class.java)

        val createdEndpoint = createResult.response.getHeader("Location").toString()

        val updateRequest = RequestUpdateEmployeeDTO(entity.firstname, entity.lastname, entity.joinDate!!, ZonedDateTime.now())

        restUserMockMvc.perform(put(createdEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(entity.id!!))
                .andExpect(jsonPath("$.firstname").value(entity.firstname!!))
                .andExpect(jsonPath("$.lastname").value(entity.lastname!!))
                .andExpect(jsonPath("$.joinDate").value(notNullValue()))
                .andExpect(jsonPath("$.resignDate").value(notNullValue()))
                .andExpect(jsonPath("$.createdBy").value(notNullValue()))
                .andExpect(jsonPath("$.createdDate").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedBy").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate").value(notNullValue()))
    }

    @Test
    fun `Assert get list of employees`() {

        val result = restUserMockMvc.perform(get("/api/employees")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andReturn()

        assertThat(result.response.getHeader("x-total-count")!!.toLong()).isGreaterThan(0)
        assertThat(result.response.getHeader("x-total-page")!!.toLong()).isGreaterThan(0)
        assertThat(result.response.getHeader("x-current-page")!!.toLong()).isEqualTo(0)
        assertThat(result.response.getHeader("x-page-size")!!.toLong()).isGreaterThan(0)
        assertThat(result.response.contentAsString).isNotBlank()
    }

    @Test
    fun `Assert get employee by id`() {
        restUserMockMvc.perform(get("/api/employees/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.id").value(notNullValue()))
                .andExpect(jsonPath("$.firstname").value(notNullValue()))
                .andExpect(jsonPath("$.lastname").value(notNullValue()))
                .andExpect(jsonPath("$.joinDate").value(notNullValue()))
                .andExpect(jsonPath("$.createdBy").value(notNullValue()))
                .andExpect(jsonPath("$.createdDate").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedBy").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedDate").value(notNullValue()))
    }
}
