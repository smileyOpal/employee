package com.interview.assignment.web.rest

import com.interview.assignment.service.EmployeeService
import com.interview.assignment.service.dto.RequestCreateEmployeeDTO
import com.interview.assignment.service.dto.RequestUpdateEmployeeDTO
import com.interview.assignment.service.dto.ResponseEmployeeDTO
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid

/**
 * A Resource/Controller layer that defined request interfaces and receives requests
 */
@RestController
@RequestMapping("/api")
@Api("Employee resource")
class EmployeeResource(private val employeeService: EmployeeService) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/employees")
    @ApiOperation(notes = "Create employee", value = "Create new employee")
    fun createNewEmployee(@RequestBody @Valid request: RequestCreateEmployeeDTO): ResponseEntity<ResponseEmployeeDTO> {
        log.info("Request create new employee")
        val result = employeeService.createEmployee(request)
        return ResponseEntity.created(URI("/api/employees/${result.id}")).body(result)
    }

    @PutMapping("/employees/{employeeId}")
    @ApiOperation(notes = "Update employee", value = "Update existing employee")
    fun updateEmployee(@PathVariable employeeId: Long, @RequestBody @Valid request: RequestUpdateEmployeeDTO): ResponseEntity<ResponseEmployeeDTO> {
        log.info("Request update employee")
        val result = employeeService.updateEmployee(employeeId, request)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/employees")
    @ApiOperation(notes = "List all employees", value = "List employees")
    fun searchEmployees(pageable: Pageable): ResponseEntity<List<ResponseEmployeeDTO>> {
        log.info("Request search employees")
        val result = employeeService.getEmployees(pageable)
        val httpHeaders = HttpHeaders()
        httpHeaders["x-total-count"] = result.totalElements.toString()
        httpHeaders["x-total-page"] = result.totalPages.toString()
        httpHeaders["x-current-page"] = result.number.toString()
        httpHeaders["x-page-size"] = result.size.toString()
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(result.content)
    }

    @GetMapping("/employees/{employeeId}")
    @ApiOperation(notes = "Get employee by id", value = "Get a employee")
    fun getEmployee(@PathVariable employeeId: Long): ResponseEntity<ResponseEmployeeDTO> {
        log.info("Request get employee by id '$employeeId'")
        val employee = employeeService.getEmployee(employeeId)
        if (employee.isPresent) return ResponseEntity.ok(employee.get())
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping("/employees/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(notes = "Delete employee by id", value = "Delete a employee")
    fun deleteEmployee(@PathVariable employeeId: Long) {
        log.info("Request delete employee id '$employeeId'")
        employeeService.deleteEmployee(employeeId)
    }
}