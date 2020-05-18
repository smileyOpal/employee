package com.interview.assignment.service.dto

import java.time.ZonedDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

/**
 * A DTO for passing in create employee request
 */
data class RequestCreateEmployeeDTO(@field:NotNull
                                    @field:NotEmpty
                                    val firstname: String? = null,

                                    @field:NotNull
                                    @field:NotEmpty
                                    val lastname: String? = null)

/**
 * A DTO for passing in update employee request
 */
data class RequestUpdateEmployeeDTO(@field:NotNull
                                    @field:NotEmpty
                                    val firstname: String? = null,

                                    @field:NotNull
                                    @field:NotEmpty
                                    val lastname: String? = null,

                                    @field:NotNull
                                    val joinDate: ZonedDateTime? = null,
                                    val resignDate: ZonedDateTime? = null)

/**
 * A DTO for passing out employee data
 */
data class ResponseEmployeeDTO(val id: Long? = null,
                               val firstname: String? = null,
                               val lastname: String? = null,
                               val joinDate: ZonedDateTime? = null,
                               val resignDate: ZonedDateTime? = null,
                               val createdBy: String,
                               val createdDate: ZonedDateTime? = null,
                               val lastModifiedBy: String,
                               val lastModifiedDate: ZonedDateTime? = null) {
    constructor() : this(null, null, null, null, null, "", null, "", null)
}