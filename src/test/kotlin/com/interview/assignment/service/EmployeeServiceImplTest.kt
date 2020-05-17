package com.interview.assignment.service

import com.interview.assignment.domain.Employee
import com.interview.assignment.repository.EmployeeRepository
import com.interview.assignment.service.dto.RequestCreateEmployeeDTO
import com.interview.assignment.service.dto.RequestUpdateEmployeeDTO
import com.interview.assignment.service.mapper.EmployeeMapper
import com.interview.assignment.web.rest.error.NotFoundException
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.fail
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import java.time.ZonedDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class EmployeeServiceImplTest {

    private lateinit var employeeService: EmployeeService

    private var employeeRepository = mockk<EmployeeRepository>()
    private var employeeMapper = EmployeeMapper()

    private val employee = Employee()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        employeeService = EmployeeServiceImpl(employeeMapper, employeeRepository)
        val currentDateTime = ZonedDateTime.now()
        employee.id = 1L
        employee.firstname = "Severus"
        employee.lastname = "Snape"
        employee.resignDate = currentDateTime
        employee.joinDate = currentDateTime.minusDays(10)
        employee.createdBy = "system"
        employee.createdDate = currentDateTime
        employee.lastModifiedBy = "system"
        employee.lastModifiedDate = currentDateTime
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `Assert when get all employees`() {
        every { employeeRepository.findAll(Pageable.unpaged()) } returns PageImpl(listOf(employee))
        val actual = employeeService.getEmployees(Pageable.unpaged())
        assertThat(actual.content).hasSize(1)
        assertThat(actual.content[0].id).isEqualTo(employee.id)
        assertThat(actual.content[0].firstname).isEqualTo(employee.firstname)
        assertThat(actual.content[0].lastname).isEqualTo(employee.lastname)
        assertThat(actual.content[0].joinDate).isEqualTo(employee.joinDate)
        assertThat(actual.content[0].resignDate).isEqualTo(employee.resignDate)
        assertThat(actual.content[0].createdBy).isEqualTo(employee.createdBy)
        assertThat(actual.content[0].createdDate).isEqualTo(employee.createdDate)
        assertThat(actual.content[0].lastModifiedBy).isEqualTo(employee.lastModifiedBy)
        assertThat(actual.content[0].lastModifiedDate).isEqualTo(employee.lastModifiedDate)
        verify { employeeRepository.findAll(Pageable.unpaged()) }
        confirmVerified(employeeRepository)
    }

    @Test
    fun `Assert when get employee by id`() {
        every { employeeRepository.findById(1L) } returns Optional.of(employee)
        val actual = employeeService.getEmployee(1L).get()
        assertThat(actual.id).isEqualTo(employee.id)
        assertThat(actual.firstname).isEqualTo(employee.firstname)
        assertThat(actual.lastname).isEqualTo(employee.lastname)
        assertThat(actual.joinDate).isEqualTo(employee.joinDate)
        assertThat(actual.resignDate).isEqualTo(employee.resignDate)
        assertThat(actual.createdBy).isEqualTo(employee.createdBy)
        assertThat(actual.createdDate).isEqualTo(employee.createdDate)
        assertThat(actual.lastModifiedBy).isEqualTo(employee.lastModifiedBy)
        assertThat(actual.lastModifiedDate).isEqualTo(employee.lastModifiedDate)
        verify { employeeRepository.findById(1L) }
        confirmVerified(employeeRepository)
    }

    @Test
    fun `Assert when delete employee by id`() {
        every { employeeRepository.existsById(1L) } returns true
        every { employeeRepository.deleteById(1L) } just Runs
        employeeService.deleteEmployee(1L)
        verifySequence {
            employeeRepository.existsById(1L)
            employeeRepository.deleteById(1L)
        }
    }

    @Test
    fun `Assert when delete employee with non-exist id`() {
        every { employeeRepository.existsById(1L) } returns false
        try {
            employeeService.deleteEmployee(1L)
            fail { "Expect NotFoundException" }
        } catch (ex: NotFoundException) {
            verify { employeeRepository.existsById(1L) }
            verify(exactly = 0) { employeeRepository.deleteById(1L) }
            confirmVerified(employeeRepository)
        } catch (ex: Exception) {
            fail { "Expect NotFoundException" }
        }

    }

    @Test
    fun `Assert when create new employee`() {
        val request = RequestCreateEmployeeDTO("Cho", "Chang")
        every { employeeRepository.save(any<Employee>()) } returns employee
        employeeService.createEmployee(request)
        verify { employeeRepository.save(any<Employee>()) }
        confirmVerified(employeeRepository)
    }

    @Test
    fun `Assert when update employee`() {
        every { employeeRepository.findByIdOrNull(1L) } returns employee
        every { employeeRepository.save(any<Employee>()) } returns employee
        employeeService.updateEmployee(1L,
                RequestUpdateEmployeeDTO(employee.firstname, employee.lastname, ZonedDateTime.now(), null))
        verifySequence {
            employeeRepository.findByIdOrNull(1L)
            employeeRepository.save(any<Employee>())
        }
        confirmVerified(employeeRepository)
    }

    @Test
    fun `Assert when update non-exist employee`() {
        every { employeeRepository.findByIdOrNull(1L) } returns null
        try {
            employeeService.updateEmployee(1L,
                    RequestUpdateEmployeeDTO(employee.firstname, employee.lastname, ZonedDateTime.now(), null))
            fail { "Expect NotFoundException" }
        } catch(ex: NotFoundException) {
            verify { employeeRepository.findByIdOrNull(1L) }
            verify(exactly = 0) { employeeRepository.save(any<Employee>()) }
            confirmVerified(employeeRepository)
        } catch (ex: Exception) {
            fail { "Expect NotFoundException" }
        }
    }
}