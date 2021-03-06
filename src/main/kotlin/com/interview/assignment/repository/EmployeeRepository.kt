package com.interview.assignment.repository

import com.interview.assignment.domain.Employee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * A Persistence layer that handle data access of employee entity
 */
@Repository
interface EmployeeRepository : JpaRepository<Employee, Long>