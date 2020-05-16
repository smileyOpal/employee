package com.interview.assignment.service

import com.interview.assignment.repository.EmployeeRepository
import com.interview.assignment.service.dto.RequestCreateEmployeeDTO
import com.interview.assignment.service.dto.RequestUpdateEmployeeDTO
import com.interview.assignment.service.dto.ResponseEmployeeDTO
import com.interview.assignment.service.mapper.EmployeeMapper
import com.interview.assignment.web.rest.error.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

interface EmployeeService {
    fun getEmployees(pageable: Pageable): Page<ResponseEmployeeDTO>
    fun createEmployee(request: RequestCreateEmployeeDTO): ResponseEmployeeDTO
    fun updateEmployee(employeeId: Long, request: RequestUpdateEmployeeDTO): ResponseEmployeeDTO
    fun getEmployee(employeeId: Long): Optional<ResponseEmployeeDTO>
    fun deleteEmployee(employeeId: Long)
}

@Service
class EmployeeServiceImpl(private val employeeMapper: EmployeeMapper,
                          private val employeeRepository: EmployeeRepository) : EmployeeService {

    override fun getEmployees(pageable: Pageable): Page<ResponseEmployeeDTO> {
        val result = employeeRepository.findAll(pageable)
        return result.map { employeeMapper.employeeToResponseEmployeeDTO(it) }
    }

    override fun createEmployee(request: RequestCreateEmployeeDTO): ResponseEmployeeDTO {
        val result = employeeRepository.save(employeeMapper.requestCreateEmployeeDtoToEmployee(request))
        return employeeMapper.employeeToResponseEmployeeDTO(result)
    }

    override fun updateEmployee(employeeId: Long, request: RequestUpdateEmployeeDTO): ResponseEmployeeDTO {
        val employee = employeeRepository.findByIdOrNull(employeeId)
                ?: throw NotFoundException("Employee ID not found for update")
        employee.firstname = request.firstname
        employee.lastname = request.lastname
        employee.joinDate = request.joinDate
        employee.resignDate = request.resignDate
        val result = employeeRepository.save(employee)
        return employeeMapper.employeeToResponseEmployeeDTO(result)
    }

    override fun getEmployee(employeeId: Long): Optional<ResponseEmployeeDTO> {
        return employeeRepository.findById(employeeId).map { employeeMapper.employeeToResponseEmployeeDTO(it) }
    }

    override fun deleteEmployee(employeeId: Long) {
        if (employeeRepository.existsById(employeeId)) throw NotFoundException("Employee ID not found for delete")
        employeeRepository.deleteById(employeeId)
    }
}