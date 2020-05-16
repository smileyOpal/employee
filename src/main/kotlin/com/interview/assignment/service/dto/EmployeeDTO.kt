package com.interview.assignment.service.dto

import java.time.ZonedDateTime
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

data class RequestCreateEmployeeDTO(@field:NotNull
                                    @field:NotEmpty
                                    val firstname: String? = null,

                                    @field:NotNull
                                    @field:NotEmpty
                                    val lastname: String? = null)

data class RequestUpdateEmployeeDTO(@field:NotNull
                                    @field:NotEmpty
                                    val firstname: String? = null,

                                    @field:NotNull
                                    @field:NotEmpty
                                    val lastname: String? = null,

                                    @field:NotNull
                                    val joinDate: ZonedDateTime,
                                    val resignDate: ZonedDateTime? = null)

data class ResponseEmployeeDTO(val id: Long,
                               val firstname: String? = null,
                               val lastname: String? = null,
                               val joinDate: ZonedDateTime,
                               val resignDate: ZonedDateTime? = null,
                               val createdBy: String,
                               val createdDate: ZonedDateTime,
                               val lastModifiedBy: String,
                               val lastModifiedDate: ZonedDateTime)