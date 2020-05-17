package com.interview.assignment.service.mapper

import com.interview.assignment.domain.Employee
import com.interview.assignment.service.dto.RequestCreateEmployeeDTO
import com.interview.assignment.service.dto.ResponseEmployeeDTO
import org.springframework.stereotype.Component
import java.time.ZonedDateTime

@Component
class EmployeeMapper {
    fun employeeToResponseEmployeeDTO(employee: Employee): ResponseEmployeeDTO {
        return ResponseEmployeeDTO(id = employee.id!!,
                firstname = employee.firstname,
                lastname = employee.lastname,
                joinDate = employee.joinDate,
                resignDate = employee.resignDate,
                createdBy = employee.createdBy,
                createdDate = employee.createdDate,
                lastModifiedBy = employee.lastModifiedBy,
                lastModifiedDate = employee.lastModifiedDate)
    }

    fun requestCreateEmployeeDtoToEmployee(dto: RequestCreateEmployeeDTO): Employee {
        val employee = Employee()
        employee.firstname = dto.firstname
        employee.lastname = dto.lastname
        employee.joinDate = ZonedDateTime.now()
        return employee
    }
}