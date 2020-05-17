package com.interview.assignment.service

import com.interview.assignment.repository.EmployeeRepository
import com.interview.assignment.service.dto.RequestCreateEmployeeDTO
import com.interview.assignment.service.dto.RequestUpdateEmployeeDTO
import com.interview.assignment.service.dto.ResponseEmployeeDTO
import com.interview.assignment.service.mapper.EmployeeMapper
import com.interview.assignment.web.rest.error.BadRequestException
import com.interview.assignment.web.rest.error.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

/**
 * A Service layer than handle all the CRUD logic for employee entity
 */
interface EmployeeService {
    /**
     * Return a page of employees
     *
     * @param   pageable
     * @return  page object with content ResponseEmployeeDTO
     * @see     Page
     * @see     ResponseEmployeeDTO
     */
    fun getEmployees(pageable: Pageable): Page<ResponseEmployeeDTO>

    /**
     * Create new employee
     *
     * @param request
     * @return created employee response DTO
     * @see RequestCreateEmployeeDTO
     * @see ResponseEmployeeDTO
     */
    fun createEmployee(request: RequestCreateEmployeeDTO): ResponseEmployeeDTO

    /**
     * Update existing employee
     *
     * @param employeeId
     * @param request
     * @throws NotFoundException if employee entity not found
     * @throws BadRequestException if joinDate is after resignDate
     * @see RequestUpdateEmployeeDTO
     * @see ResponseEmployeeDTO
     */
    fun updateEmployee(employeeId: Long, request: RequestUpdateEmployeeDTO): ResponseEmployeeDTO

    /**
     * Return employee
     *
     * @param employeeId
     * @return optional of employee
     * @see ResponseEmployeeDTO
     */
    fun getEmployee(employeeId: Long): Optional<ResponseEmployeeDTO>

    /**
     * Delete employee by id
     *
     * @param employeeId
     * @throws NotFoundException if employee entity not found
     */
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
        validate(request)
        val employee = employeeRepository.findByIdOrNull(employeeId)
                ?: throw NotFoundException("Employee ID not found for update")
        employee.firstname = request.firstname
        employee.lastname = request.lastname
        employee.joinDate = request.joinDate
        employee.resignDate = request.resignDate

        val result = employeeRepository.save(employee)
        return employeeMapper.employeeToResponseEmployeeDTO(result)
    }

    /**
     * Validate employee update request
     *
     * @throws BadRequestException if resignDate has value but its value is before joinDate
     */
    private fun validate(request: RequestUpdateEmployeeDTO) {
        if (request.resignDate != null && request.joinDate.isAfter(request.resignDate)) {
            throw BadRequestException("Employee joined date must be before resigned date")
        }
    }

    override fun getEmployee(employeeId: Long): Optional<ResponseEmployeeDTO> {
        return employeeRepository.findById(employeeId).map { employeeMapper.employeeToResponseEmployeeDTO(it) }
    }

    override fun deleteEmployee(employeeId: Long) {
        if (!employeeRepository.existsById(employeeId)) throw NotFoundException("Employee ID not found for delete")
        employeeRepository.deleteById(employeeId)
    }
}