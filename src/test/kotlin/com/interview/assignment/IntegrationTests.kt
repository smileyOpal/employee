package com.interview.assignment

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.interview.assignment.service.dto.RequestCreateEmployeeDTO
import com.interview.assignment.service.dto.RequestUpdateEmployeeDTO
import com.interview.assignment.service.dto.ResponseEmployeeDTO
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.ZonedDateTime


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTests(@Autowired private val restTemplate: TestRestTemplate) {

    private var mapper = ObjectMapper()

    init {
        mapper.registerModule(Jdk8Module())
        mapper.registerModule(JavaTimeModule())
    }

    @Test
    fun `Assert create new employee and delete newly create employee`() {
        val entity = restTemplate.postForEntity("/api/employees", RequestCreateEmployeeDTO("integration", "test"), ResponseEmployeeDTO::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.CREATED)

        assertThat(entity.body).isNotNull()
        assertThat(entity.body!!.id).isNotNull()
        assertThat(entity.body!!.firstname).isNotNull()
        assertThat(entity.body!!.lastname).isNotNull()
        assertThat(entity.body!!.joinDate).isNotNull()
        assertThat(entity.body!!.createdBy).isNotNull()
        assertThat(entity.body!!.createdDate).isNotNull()
        assertThat(entity.body!!.lastModifiedBy).isNotNull()
        assertThat(entity.body!!.lastModifiedDate).isNotNull()

        val result = restTemplate.exchange("/api/employees/{employeeId}", HttpMethod.DELETE, null, String::class.java, entity.body!!.id)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `Assert create new employee with empty value`() {
        val entity = restTemplate.postForEntity("/api/employees", RequestCreateEmployeeDTO("", ""), String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)

        val root: JsonNode = mapper.readTree(entity.body)
        assertThat(root.path("status").asLong()).isEqualTo(400)
        assertThat(root.path("error").asText()).isEqualTo("Bad Request")
        assertThat(root.path("path").asText()).isEqualTo("/api/employees")
        assertThat(root.path("message").asText()).isEqualTo("Validation failed for object='requestCreateEmployeeDTO'. Error count: 2")

        val errors: JsonNode = root.path("errors")
        assertThat(errors.findValuesAsText("code")).containsExactlyInAnyOrder("lastname", "firstname", "NotEmpty", "NotEmpty")
        assertThat(errors.findValuesAsText("field")).containsExactlyInAnyOrder("lastname", "firstname")
        assertThat(errors.findValuesAsText("defaultMessage")).containsExactlyInAnyOrder("lastname", "firstname", "must not be empty", "must not be empty")
        assertThat(errors.findValuesAsText("objectName")).containsExactlyInAnyOrder("requestCreateEmployeeDTO", "requestCreateEmployeeDTO")
    }

    @Test
    fun `Assert create new employee with null value`() {
        val entity = restTemplate.postForEntity("/api/employees", RequestCreateEmployeeDTO(null, null), String::class.java)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)


        val root: JsonNode = mapper.readTree(entity.body)
        assertThat(root.path("status").asLong()).isEqualTo(400)
        assertThat(root.path("error").asText()).isEqualTo("Bad Request")
        assertThat(root.path("path").asText()).isEqualTo("/api/employees")
        assertThat(root.path("message").asText()).isEqualTo("Validation failed for object='requestCreateEmployeeDTO'. Error count: 4")

        val errors: JsonNode = root.path("errors")
        assertThat(errors.findValuesAsText("code")).containsExactlyInAnyOrder("lastname", "firstname", "NotNull", "NotNull", "lastname", "firstname", "NotEmpty", "NotEmpty")
        assertThat(errors.findValuesAsText("field")).containsExactlyInAnyOrder("lastname", "firstname", "lastname", "firstname")
        assertThat(errors.findValuesAsText("defaultMessage")).containsExactlyInAnyOrder("lastname", "firstname", "must not be null", "must not be null", "lastname", "firstname", "must not be empty", "must not be empty")
        assertThat(errors.findValuesAsText("objectName")).containsExactlyInAnyOrder("requestCreateEmployeeDTO", "requestCreateEmployeeDTO", "requestCreateEmployeeDTO", "requestCreateEmployeeDTO")
    }

    @Test
    fun `Assert update employee`() {
        // list employees and get first
        val entities = restTemplate.getForEntity<List<ResponseEmployeeDTO>>("/api/employees").body!!
        val entity = mapper.convertValue(entities[0], ResponseEmployeeDTO::class.java)
        val request = RequestUpdateEmployeeDTO(entity.firstname, entity.lastname, entity.joinDate!!, ZonedDateTime.now())

        val result = restTemplate.exchange("/api/employees/{employeeId}", HttpMethod.PUT, HttpEntity(request), ResponseEmployeeDTO::class.java, entity.id)
        assertThat(result.statusCode).isEqualTo(HttpStatus.OK)

        assertThat(result.body).isNotNull()
        assertThat(result.body!!.id).isEqualTo(entity.id)
        assertThat(result.body!!.firstname).isEqualTo(entity.firstname)
        assertThat(result.body!!.lastname).isEqualTo(entity.lastname)
        assertThat(result.body!!.joinDate).isEqualTo(entity.joinDate)
        assertThat(result.body!!.resignDate).isEqualTo(request.resignDate)
        assertThat(result.body!!.createdBy).isEqualTo(entity.createdBy)
        assertThat(result.body!!.createdDate).isEqualTo(entity.createdDate)
        assertThat(result.body!!.lastModifiedBy).isEqualTo(entity.lastModifiedBy)
        assertThat(result.body!!.lastModifiedDate).isAfter(entity.lastModifiedDate)
    }

    @Test
    fun `Assert get list of employees`() {
        val entity = restTemplate.getForEntity<List<ResponseEmployeeDTO>>("/api/employees")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.headers["x-total-count"]!![0].toLong()).isGreaterThan(0)
        assertThat(entity.headers["x-total-page"]!![0].toLong()).isGreaterThan(0)
        assertThat(entity.headers["x-current-page"]!![0].toLong()).isEqualTo(0)
        assertThat(entity.headers["x-page-size"]!![0].toLong()).isGreaterThan(0)
        assertThat(entity.body).isNotEmpty()
    }

    @Test
    fun `Assert get employee by id`() {
        val entity = restTemplate.getForEntity<ResponseEmployeeDTO>("/api/employees/1")
        assertThat(entity.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(entity.body).isNotNull()
        assertThat(entity.body!!.id).isEqualTo(1)
        assertThat(entity.body!!.firstname).isNotNull()
        assertThat(entity.body!!.lastname).isNotNull()
        assertThat(entity.body!!.joinDate).isNotNull()
        assertThat(entity.body!!.createdBy).isNotNull()
        assertThat(entity.body!!.createdDate).isNotNull()
        assertThat(entity.body!!.lastModifiedBy).isNotNull()
        assertThat(entity.body!!.lastModifiedDate).isNotNull()
    }

}