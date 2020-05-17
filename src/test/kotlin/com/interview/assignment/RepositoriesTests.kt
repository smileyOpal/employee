package com.interview.assignment

import com.interview.assignment.config.DatabaseConfig
import com.interview.assignment.domain.Employee
import com.interview.assignment.repository.EmployeeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.context.annotation.FilterType
import java.time.ZonedDateTime

@DataJpaTest(includeFilters = [Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [DatabaseConfig::class])])
class RepositoriesTests @Autowired constructor(private val entityManager: TestEntityManager,
                                               private val employeeRepository: EmployeeRepository) {

    @Test
    fun `Assert audit fields when save new entity return Employee and then update and delete`() {
        val employee = Employee()
        employee.firstname = "Dudley"
        employee.lastname = "Dursley"
        employee.joinDate = ZonedDateTime.now()
        val before = employeeRepository.count()
        val actual = employeeRepository.saveAndFlush(employee)
        assertThat(employeeRepository.count()).isGreaterThan(before)
        assertThat(actual.id).isNotNull()
        assertThat(actual.firstname).isEqualTo(employee.firstname)
        assertThat(actual.lastname).isEqualTo(employee.lastname)
        assertThat(actual.joinDate).isNotNull()
        assertThat(actual.resignDate).isNull()
        assertThat(actual.createdBy).isNotNull()
        assertThat(actual.createdDate).isNotNull()
        assertThat(actual.lastModifiedBy).isNotNull()
        assertThat(actual.lastModifiedDate).isNotNull()

        actual.resignDate = ZonedDateTime.now()
        val updated = employeeRepository.saveAndFlush(actual)
        assertThat(updated.id).isEqualTo(actual.id)
        assertThat(updated.firstname).isEqualTo(actual.firstname)
        assertThat(updated.lastname).isEqualTo(actual.lastname)
        assertThat(updated.joinDate).isEqualTo(actual.joinDate)
        assertThat(updated.resignDate).isNotNull()
        assertThat(updated.createdBy).isEqualTo(actual.createdBy)
        assertThat(updated.createdDate).isEqualTo(actual.createdDate)
        assertThat(updated.lastModifiedBy).isNotNull()
        assertThat(updated.lastModifiedDate).isAfterOrEqualTo(actual.lastModifiedDate)

        employeeRepository.deleteById(actual.id!!)
        assertThat(employeeRepository.count()).isEqualTo(before)
        assertThat(employeeRepository.existsById(actual.id!!)).isFalse()
    }
}